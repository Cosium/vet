package com.cosium.vet.gerrit;

import com.cosium.vet.gerrit.config.GerritConfigurationRepository;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.GitUtils;
import com.cosium.vet.utils.NonBlankString;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class DefaultGerritClient implements GerritClient {

  private final GerritConfigurationRepository configurationRepository;
  private final ChangeChangeIdFactory changeChangeIdFactory;
  private final GitClient git;
  private final GerritPushUrl pushUrl;

  DefaultGerritClient(
      GerritConfigurationRepository configurationRepository,
      ChangeChangeIdFactory changeChangeIdFactory,
      GitClient gitClient,
      GerritPushUrl pushUrl) {
    requireNonNull(configurationRepository);
    requireNonNull(gitClient);
    requireNonNull(pushUrl);

    this.configurationRepository = configurationRepository;
    this.changeChangeIdFactory = changeChangeIdFactory;
    this.git = gitClient;
    this.pushUrl = pushUrl;
  }

  private ChangeChangeId buildChangeChangeId(BranchShortName targetBranch) {
    return changeChangeIdFactory.build(git.getBranch(), targetBranch);
  }

  @Override
  public Optional<GerritChange> getChange() {
    return configurationRepository
        .read()
        .getChangeTargetBranch()
        .map(
            targetBranch ->
                new DefaultGerritChange(buildChangeChangeId(targetBranch), targetBranch));
  }

  @Override
  public GerritChange setAndGetChange(BranchShortName targetBranch) {
    return configurationRepository.readAndWrite(
        conf -> {
          GerritChange change =
              new DefaultGerritChange(buildChangeChangeId(targetBranch), targetBranch);
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

    String commitMessage =
        String.format("%s\n\nChange-Id: %s", git.getLastCommitMessage(), theChange.getChangeId());
    String commitId = git.commitTree(endRevision, startRevision, commitMessage);

    String messageSuffix =
        ofNullable(subject)
            .map(NonBlankString::toString)
            .map(GitUtils::encodeForGitRef)
            .map(s -> String.format("m=%s", s))
            .orElse(StringUtils.EMPTY);

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
    private final BranchShortName targetBranch;

    DefaultGerritChange(ChangeChangeId changeId, BranchShortName targetBranch) {
      requireNonNull(changeId);
      requireNonNull(targetBranch);

      this.changeId = changeId;
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
