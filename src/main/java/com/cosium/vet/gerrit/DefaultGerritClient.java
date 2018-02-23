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
  private final GitClient git;
  private final GerritPushUrl pushUrl;
  private final GerritProjectName project;

  DefaultGerritClient(
      GerritConfigurationRepository configurationRepository,
      GitClient gitClient,
      GerritPushUrl pushUrl,
      GerritProjectName project) {
    requireNonNull(configurationRepository);
    requireNonNull(gitClient);
    requireNonNull(pushUrl);
    requireNonNull(project);

    this.configurationRepository = configurationRepository;
    this.git = gitClient;
    this.pushUrl = pushUrl;
    this.project = project;
  }

  private ChangeChangeId buildChangeChangeId(BranchShortName targetBranch) {
    return ChangeChangeId.builder()
        .project(project)
        .sourceBranch(git.getBranch())
        .targetBranch(targetBranch)
        .build();
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
