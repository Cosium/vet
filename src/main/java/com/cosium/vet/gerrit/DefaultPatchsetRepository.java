package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchRef;
import com.cosium.vet.git.BranchRefName;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.CommitMessage;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.RemoteName;
import com.cosium.vet.git.RevisionId;
import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Created on 27/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultPatchsetRepository implements PatchsetRepository {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultPatchsetRepository.class);

  private static final Pattern BRANCH_REF_CHANGE_PATTERN =
      Pattern.compile("refs/changes/\\d{2}/(\\d+)/(\\d+)");

  private final GitClient git;
  private final PushUrl pushUrl;
  private final PatchsetCommitMessageFactory commitMessageFactory;
  private final TargetBranch.Factory targetBranchFactory;

  DefaultPatchsetRepository(
      GitClient gitClient,
      PushUrl pushUrl,
      PatchsetCommitMessageFactory commitMessageFactory,
      TargetBranch.Factory targetBranchFactory) {
    this.git = requireNonNull(gitClient);
    this.pushUrl = requireNonNull(pushUrl);
    this.commitMessageFactory = requireNonNull(commitMessageFactory);
    this.targetBranchFactory = requireNonNull(targetBranchFactory);
  }

  @Override
  public CreatedPatchset createChangeFirstPatchset(
      BranchShortName targetBranchShortName, PatchsetOptions options) {

    CommitMessage commitMessage = commitMessageFactory.build();

    RevisionId startRevision =
        targetBranchFactory.build(targetBranchShortName).computeChangeStartRevision();

    String endRevision = git.getTree();
    LOG.debug(
        "Creating first patchset for new change between start revision '{}' and end revision '{}'",
        startRevision,
        endRevision);

    LOG.debug("Creating commit tree with message '{}'", commitMessage);
    String commitId = git.commitTree(endRevision, startRevision.toString(), commitMessage);
    LOG.debug("Commit tree id is '{}'", commitId);

    LOG.debug("Pushing '{}' to '{}', with options '{}'", commitId, targetBranchShortName, options);

    String creationLog =
        git.push(pushUrl.toString(), options.buildGitPushTarget(commitId, targetBranchShortName));

    ChangeNumericId changeNumericId =
        ChangeNumericId.parseFromPushToRefForOutput(pushUrl, creationLog);

    return new DefaultCreatedPatchset(
        1, changeNumericId, RevisionId.of(commitId), startRevision, commitMessage, creationLog);
  }

  @Override
  public CreatedPatchset createPatchset(
      BranchShortName targetBranchShortName, ChangeNumericId numericId, PatchsetOptions options) {

    Patchset latestPatchset =
        findLatestPatchset(numericId)
            .orElseThrow(
                () -> new RuntimeException("No patchset found for change numeric id " + numericId));
    CommitMessage commitMessage = commitMessageFactory.build(latestPatchset);

    RevisionId startRevision =
        targetBranchFactory.build(targetBranchShortName).computeChangeStartRevision();

    String endRevision = git.getTree();
    LOG.debug(
        "Creating patchset for change '{}' between start revision '{}' and end revision '{}'",
        numericId,
        startRevision,
        endRevision);

    LOG.debug("Creating commit tree with message '{}'", commitMessage);
    String commitId = git.commitTree(endRevision, startRevision.toString(), commitMessage);
    LOG.debug("Commit tree id is '{}'", commitId);

    LOG.debug("Pushing '{}' to '{}', with options '{}'", commitId, targetBranchShortName, options);

    String creationLog =
        git.push(pushUrl.toString(), options.buildGitPushTarget(commitId, targetBranchShortName));
    return new DefaultCreatedPatchset(
        latestPatchset.getNumber(),
        numericId,
        RevisionId.of(commitId),
        startRevision,
        commitMessage,
        creationLog);
  }

  @Override
  public Optional<Patchset> findLatestPatchset(ChangeNumericId changeNumericId) {
    requireNonNull(changeNumericId);

    PatchsetRef latestPatchsetRef = getLatestPatchsetRef(changeNumericId).orElse(null);
    if (latestPatchsetRef == null) {
      LOG.debug("No revision found for change {}", changeNumericId);
      return Optional.empty();
    }
    return Optional.of(buildPatchset(latestPatchsetRef));
  }

  @Override
  public Patchset findPatchset(ChangeNumericId changeNumericId, int patchsetNumber) {
    PatchsetRef patchsetRef =
        getPatchsetRef(changeNumericId, patchsetNumber)
            .orElseThrow(
                () ->
                    new RuntimeException(
                        "Could not find patchset ref for change numeric id "
                            + changeNumericId
                            + " and patchset number "
                            + patchsetNumber));
    return buildPatchset(patchsetRef);
  }

  @Override
  public String pullLatestPatchset(ChangeNumericId changeNumericId) {
    Patchset latestPatchset =
        findLatestPatchset(changeNumericId)
            .orElseThrow(
                () ->
                    new RuntimeException(
                        "No patchset found for change with id " + changeNumericId));
    BranchRefName refName = changeNumericId.branchRefName(latestPatchset);
    return git.pull(RemoteName.ORIGIN, refName);
  }

  /**
   * @param changeNumericId The targeted change numeric id
   * @return The latest revision for the provided change numeric id
   */
  private Optional<PatchsetRef> getLatestPatchsetRef(ChangeNumericId changeNumericId) {
    return getPatchsetRefs(changeNumericId).stream()
        .max(Comparator.comparingInt(PatchsetRef::getNumber));
  }

  private Optional<PatchsetRef> getPatchsetRef(
      ChangeNumericId changeNumericId, int patchsetNumber) {
    return getPatchsetRefs(changeNumericId).stream()
        .filter(patchsetRef -> patchsetRef.getNumber() == patchsetNumber)
        .findFirst();
  }

  private List<PatchsetRef> getPatchsetRefs(ChangeNumericId changeNumericId) {
    return git.listRemoteRefs(RemoteName.of(pushUrl.toString())).stream()
        .map(PatchsetRefBuilder::new)
        .map(PatchsetRefBuilder::build)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(patchsetRef -> patchsetRef.getChangeNumericId().equals(changeNumericId))
        .collect(Collectors.toList());
  }

  private Patchset buildPatchset(PatchsetRef patchsetRef) {
    git.fetch(RemoteName.of(pushUrl.toString()), patchsetRef.getBranchRefName());
    RevisionId revisionId = patchsetRef.getRevisionId();
    return new DefaultPatchset(
        patchsetRef.getNumber(),
        patchsetRef.getChangeNumericId(),
        revisionId,
        git.getFirstParent(revisionId),
        git.getCommitMessage(revisionId));
  }

  private class DefaultPatchset implements Patchset {
    private final int number;
    private final ChangeNumericId changeNumericId;
    private final CommitMessage commitMessage;
    private final RevisionId revision;
    private final RevisionId parent;

    private DefaultPatchset(
        int number,
        ChangeNumericId changeNumericId,
        RevisionId revision,
        RevisionId parent,
        CommitMessage commitMessage) {
      this.number = number;
      this.changeNumericId = requireNonNull(changeNumericId);
      this.revision = requireNonNull(revision);
      this.parent = requireNonNull(parent);
      this.commitMessage = requireNonNull(commitMessage);
    }

    @Override
    public int getNumber() {
      return number;
    }

    @Override
    public ChangeNumericId getChangeNumericId() {
      return changeNumericId;
    }

    @Override
    public CommitMessage getCommitMessage() {
      return commitMessage;
    }

    @Override
    public RevisionId getParent() {
      return parent;
    }

    @Override
    public RevisionId getRevision() {
      return revision;
    }
  }

  private class DefaultCreatedPatchset extends DefaultPatchset implements CreatedPatchset {
    private final String creationLog;

    private DefaultCreatedPatchset(
        int number,
        ChangeNumericId changeNumericId,
        RevisionId revision,
        RevisionId parent,
        CommitMessage commitMessage,
        String creationLog) {
      super(number, changeNumericId, revision, parent, commitMessage);
      this.creationLog = requireNonNull(creationLog);
    }

    @Override
    public String getCreationLog() {
      return creationLog;
    }
  }

  private class PatchsetRefBuilder {

    private final BranchRef branchRef;
    private final Matcher matcher;

    private PatchsetRefBuilder(BranchRef branchRef) {
      requireNonNull(branchRef);
      this.branchRef = branchRef;
      this.matcher = BRANCH_REF_CHANGE_PATTERN.matcher(branchRef.getBranchRefName().toString());
    }

    private Optional<PatchsetRef> build() {
      if (!matcher.find()) {
        return Optional.empty();
      }
      return Optional.of(
          new PatchsetRef(
              branchRef,
              ChangeNumericId.of(Integer.parseInt(matcher.group(1))),
              Integer.parseInt(matcher.group(2))));
    }
  }

  private class PatchsetRef {
    private final BranchRef branchRef;
    private final ChangeNumericId changeNumericId;
    private final int number;

    private PatchsetRef(BranchRef branchRef, ChangeNumericId changeNumericId, int number) {
      this.branchRef = requireNonNull(branchRef);
      this.changeNumericId = requireNonNull(changeNumericId);
      this.number = number;
    }

    public BranchRefName getBranchRefName() {
      return branchRef.getBranchRefName();
    }

    public ChangeNumericId getChangeNumericId() {
      return changeNumericId;
    }

    public RevisionId getRevisionId() {
      return branchRef.getRevisionId();
    }

    public int getNumber() {
      return number;
    }
  }
}
