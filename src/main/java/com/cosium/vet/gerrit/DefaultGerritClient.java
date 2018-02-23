package com.cosium.vet.gerrit;

import com.cosium.vet.gerrit.config.GerritConfigurationRepository;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.GitUtils;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

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
  public GerritChange createAndSetChange(BranchShortName targetBranch, ChangeSubject subject) {
    return configurationRepository.readAndWrite(
        conf -> {
          GerritChange change =
              new DefaultGerritChange(buildChangeChangeId(targetBranch), targetBranch, subject);
          conf.setChangeTargetBranch(targetBranch);
          return change;
        });
  }

  @Override
  public void createPatchSet(
      GerritChange change, String startRevision, String endRevision, String patchSetTitle) {
    if (!(change instanceof DefaultGerritChange)) {
      throw new RuntimeException("change must be an instance of " + DefaultGerritChange.class);
    }
    DefaultGerritChange theChange = (DefaultGerritChange) change;

    String commitMessage =
        String.format("%s\n\nChange-Id: %s", theChange.getSubject().get(), theChange.getChangeId());
    String commitId = git.commitTree(git.getTree(), startRevision, commitMessage);

    git.push(
        pushUrl.toString(),
        String.format(
            "%s:refs/for/%s%%m=%s",
            commitId, theChange.getTargetBranch(), GitUtils.encodeForGitRef(patchSetTitle)));
  }

  /**
   * Created on 21/02/18.
   *
   * @author Reda.Housni-Alaoui
   */
  private class DefaultGerritChange implements GerritChange {

    private final ChangeChangeId changeId;
    private final BranchShortName targetBranch;
    private final ChangeSubject subject;

    DefaultGerritChange(ChangeChangeId changeId, BranchShortName targetBranch) {
      this(changeId, targetBranch, null);
    }

    DefaultGerritChange(
        ChangeChangeId changeId, BranchShortName targetBranch, ChangeSubject subject) {
      requireNonNull(changeId);
      requireNonNull(targetBranch);

      this.changeId = changeId;
      this.targetBranch = targetBranch;
      this.subject = subject;
    }

    ChangeChangeId getChangeId() {
      return changeId;
    }

    Optional<ChangeSubject> getSubject() {
      return Optional.ofNullable(subject);
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
      sb.append(", subject=").append(subject);
      sb.append('}');
      return sb.toString();
    }
  }
}
