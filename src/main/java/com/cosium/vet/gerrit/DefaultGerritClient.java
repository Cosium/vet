package com.cosium.vet.gerrit;

import com.cosium.vet.gerrit.config.GerritConfigurationRepository;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.GitUtils;
import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;
import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;
import com.cosium.vet.utils.NonBlankString;

import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class DefaultGerritClient implements GerritClient {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultGerritClient.class);

  private static final String COMMIT_MESSAGE_SOURCE_BRANCH_PREFIX = "Source-Branch: ";
  private static final String COMMIT_MESSAGE_CHANGE_ID_PREFIX = "Change-Id: ";

  private final GerritConfigurationRepository configurationRepository;
  private final ChangeChangeIdFactory changeChangeIdFactory;
  private final GerritPatchSetRepository patchSetRepository;
  private final GitClient git;
  private final GerritPushUrl pushUrl;

  DefaultGerritClient(
      GerritConfigurationRepository configurationRepository,
      ChangeChangeIdFactory changeChangeIdFactory,
      GerritPatchSetRepository patchSetRepository,
      GitClient gitClient,
      GerritPushUrl pushUrl) {
    requireNonNull(configurationRepository);
    requireNonNull(changeChangeIdFactory);
    requireNonNull(patchSetRepository);
    requireNonNull(gitClient);
    requireNonNull(pushUrl);

    this.configurationRepository = configurationRepository;
    this.changeChangeIdFactory = changeChangeIdFactory;
    this.patchSetRepository = patchSetRepository;
    this.git = gitClient;
    this.pushUrl = pushUrl;
  }

  private ChangeChangeId buildChangeChangeId(
      BranchShortName sourceBranch, BranchShortName targetBranch) {
    return changeChangeIdFactory.build(sourceBranch, targetBranch);
  }

  @Override
  public Optional<GerritChange> getChange() {
    BranchShortName sourceBranch = git.getBranch();
    return configurationRepository
        .read()
        .getChangeTargetBranch()
        .map(
            targetBranch ->
                new DefaultGerritChange(
                    buildChangeChangeId(sourceBranch, targetBranch), sourceBranch, targetBranch));
  }

  @Override
  public GerritChange setChange(BranchShortName targetBranch) {
    BranchShortName sourceBranch = git.getBranch();
    if (Objects.equals(sourceBranch, targetBranch)) {
      throw new RuntimeException("Target branch can't be the same as the current branch");
    }

    LOG.debug("Enabling change for target branch '{}'", targetBranch);
    return configurationRepository.readAndWrite(
        conf -> {
          GerritChange change =
              new DefaultGerritChange(
                  buildChangeChangeId(sourceBranch, targetBranch), sourceBranch, targetBranch);
          conf.setChangeTargetBranch(targetBranch);
          return change;
        });
  }

  @Override
  public void createPatchSet(
      GerritChange change, String startRevision, String endRevision, PatchSetSubject subject) {
    if (!(change instanceof DefaultGerritChange)) {
      throw new RuntimeException("change must be an instance of " + DefaultGerritChange.class);
    }
    DefaultGerritChange theChange = (DefaultGerritChange) change;

    LOG.debug(
        "Creating patch set for change '{}' between start revision '{}' and end revision '{}'",
        change,
        startRevision,
        endRevision);

    String commitMessage =
        patchSetRepository
            .getLastestPatchSetCommitMessage(pushUrl, theChange.getChangeId())
            .orElseGet(git::getLastCommitMessage)
            .removeLinesStartingWith(
                COMMIT_MESSAGE_SOURCE_BRANCH_PREFIX, COMMIT_MESSAGE_CHANGE_ID_PREFIX);

    String sourceBranchLine =
        String.format("%s%s", COMMIT_MESSAGE_SOURCE_BRANCH_PREFIX, theChange.sourceBranch);
    String changeChangeIdLine =
        String.format("%s%s", COMMIT_MESSAGE_CHANGE_ID_PREFIX, theChange.getChangeId());

    String commitMessageWithMetadata =
        String.format("%s\n\n%s\n%s", commitMessage, sourceBranchLine, changeChangeIdLine);

    LOG.debug("Create commit tree with message '{}'", commitMessageWithMetadata);

    String commitId = git.commitTree(endRevision, startRevision, commitMessageWithMetadata);
    LOG.debug("Commit tree id is '{}'", commitId);

    String messageSuffix =
        ofNullable(subject)
            .map(NonBlankString::toString)
            .map(GitUtils::encodeForGitRef)
            .map(s -> String.format("m=%s", s))
            .orElse(StringUtils.EMPTY);

    LOG.debug(
        "Pushing '{}' to '{}', with suffix '{}'",
        commitId,
        theChange.getTargetBranch(),
        messageSuffix);

    git.push(
        pushUrl.toString(),
        String.format("%s:refs/for/%s%%%s", commitId, theChange.getTargetBranch(), messageSuffix));
  }

  /**
   * Created on 21/02/18.
   *
   * @author Reda.Housni-Alaoui
   */
  private class DefaultGerritChange implements GerritChange {

    private final ChangeChangeId changeId;
    private final BranchShortName sourceBranch;
    private final BranchShortName targetBranch;

    DefaultGerritChange(
        ChangeChangeId changeId, BranchShortName sourceBranch, BranchShortName targetBranch) {
      requireNonNull(changeId);
      requireNonNull(sourceBranch);
      requireNonNull(targetBranch);

      this.changeId = changeId;
      this.sourceBranch = sourceBranch;
      this.targetBranch = targetBranch;
    }

    ChangeChangeId getChangeId() {
      return changeId;
    }

    @Override
    public BranchShortName getTargetBranch() {
      return targetBranch;
    }

    @Override
    public String toString() {
      final StringBuilder sb = new StringBuilder("GerritChange{");
      sb.append("changeId=").append(changeId);
      sb.append(", branch=").append(targetBranch);
      sb.append('}');
      return sb.toString();
    }
  }
}
