package com.cosium.vet.gerrit;

import com.cosium.vet.git.*;
import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * Created on 27/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultPatchSetRepository implements PatchSetRepository {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultPatchSetRepository.class);

  private static final Pattern BRANCH_REF_CHANGE_PATTERN =
      Pattern.compile("refs/changes/\\d{2}/(\\d+)/(\\d+)");

  private final GitClient git;
  private final PushUrl pushUrl;
  private final PatchSetCommitMessageFactory commitMessageFactory;

  DefaultPatchSetRepository(
      GitClient gitClient, PushUrl pushUrl, PatchSetCommitMessageFactory commitMessageFactory) {
    this.git = requireNonNull(gitClient);
    this.pushUrl = requireNonNull(pushUrl);
    this.commitMessageFactory = requireNonNull(commitMessageFactory);
  }

  @Override
  public Patch createPatch(
      BranchShortName targetBranch, ChangeNumericId numericId, PatchOptions options) {
    RemoteName remote =
        git.getRemote(targetBranch)
            .orElseThrow(
                () ->
                    new RuntimeException(
                        String.format("No remote found for branch '%s'", targetBranch)));
    git.fetch(remote, targetBranch);
    String startRevision =
        git.getMostRecentCommonCommit(String.format("%s/%s", remote, targetBranch));

    String endRevision = git.getTree();
    LOG.debug(
        "Creating patch set for change '{}' between start revision '{}' and end revision '{}'",
        this,
        startRevision,
        endRevision);
    Patch lastestPatch = findLastestPatch(numericId).orElse(null);
    CommitMessage commitMessage = commitMessageFactory.build(lastestPatch);

    LOG.debug("Creating commit tree with message '{}'", commitMessage);
    String commitId = git.commitTree(endRevision, startRevision, commitMessage.toString());
    LOG.debug("Commit tree id is '{}'", commitId);

    LOG.debug("Pushing '{}' to '{}', with options '{}'", commitId, targetBranch, options);

    String output = git.push(pushUrl.toString(), options.buildGitPushTarget(commitId, targetBranch));
    return buildPatch(
        lastestPatch == null ? 1 : lastestPatch.getNumber(),
        numericId,
        commitMessage,
        RevisionId.of(startRevision),
        output);
  }

  @Override
  public Optional<Patch> findLastestPatch(ChangeNumericId changeNumericId) {
    if (changeNumericId == null) {
      return Optional.empty();
    }

    PatchRef latestPatchSetRef = getLatestPatchSetRef(changeNumericId).orElse(null);
    if (latestPatchSetRef == null) {
      LOG.debug("No revision found for change {}", changeNumericId);
      return Optional.empty();
    }
    return Optional.of(buildPatch(latestPatchSetRef));
  }

  @Override
  public Patch findPatch(ChangeNumericId changeNumericId, int patchNumber) {
    PatchRef patchRef =
        getPatchSetRef(changeNumericId, patchNumber)
            .orElseThrow(
                () ->
                    new RuntimeException(
                        "Could not find patch ref for change numeric id "
                            + changeNumericId
                            + " and patch number "
                            + patchNumber));
    return buildPatch(patchRef);
  }

  @Override
  public String pullLatest(ChangeNumericId changeNumericId) {
    Patch latestPatch =
        findLastestPatch(changeNumericId)
            .orElseThrow(
                () -> new RuntimeException("No patch found for change with id " + changeNumericId));
    BranchRefName refName = changeNumericId.branchRefName(latestPatch);
    return git.pull(RemoteName.ORIGIN, refName);
  }

  /**
   * @param changeNumericId The targeted change numeric id
   * @return The latest revision for the provided change numeric id
   */
  private Optional<PatchRef> getLatestPatchSetRef(ChangeNumericId changeNumericId) {
    return getPatchSetRefs(changeNumericId)
        .stream()
        .max(Comparator.comparingInt(PatchRef::getNumber));
  }

  private Optional<PatchRef> getPatchSetRef(ChangeNumericId changeNumericId, int patchNumber) {
    return getPatchSetRefs(changeNumericId)
        .stream()
        .filter(patchSetRef -> patchSetRef.getNumber() == patchNumber)
        .findFirst();
  }

  private List<PatchRef> getPatchSetRefs(ChangeNumericId changeNumericId) {
    return git.listRemoteRefs(RemoteName.of(pushUrl.toString()))
        .stream()
        .map(PatchRefBuilder::new)
        .map(PatchRefBuilder::build)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(patchSetRef -> patchSetRef.getChangeNumericId().equals(changeNumericId))
        .collect(Collectors.toList());
  }

  private Patch buildPatch(
      int id,
      ChangeNumericId numericId,
      CommitMessage commitMessage,
      RevisionId parent,
      String pushToRefForOutput) {
    numericId =
        ofNullable(numericId)
            .orElseGet(
                () -> ChangeNumericId.parseFromPushToRefForOutput(pushUrl, pushToRefForOutput));
    return new DefaultPatch(id, numericId, parent, commitMessage, pushToRefForOutput);
  }

  private Patch buildPatch(PatchRef patchSetRef) {
    git.fetch(RemoteName.of(pushUrl.toString()), patchSetRef.getBranchRefName());
    RevisionId revisionId = patchSetRef.getRevisionId();
    return new DefaultPatch(
        patchSetRef.getNumber(),
        patchSetRef.getChangeNumericId(),
        git.getParent(revisionId),
        git.getCommitMessage(revisionId));
  }

  private class DefaultPatch implements Patch {
    private final int number;
    private final ChangeNumericId changeNumericId;
    private final CommitMessage commitMessage;
    private final RevisionId parent;
    private final String creationLog;

    private DefaultPatch(
        int number,
        ChangeNumericId changeNumericId,
        RevisionId parent,
        CommitMessage commitMessage) {
      this(number, changeNumericId, parent, commitMessage, null);
    }

    private DefaultPatch(
        int number,
        ChangeNumericId changeNumericId,
        RevisionId parent,
        CommitMessage commitMessage,
        String creationLog) {
      this.number = number;
      this.changeNumericId = requireNonNull(changeNumericId);
      this.parent = requireNonNull(parent);
      this.commitMessage = requireNonNull(commitMessage);
      this.creationLog = creationLog;
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
    public Optional<String> getCreationLog() {
      return Optional.ofNullable(creationLog);
    }
  }

  private class PatchRefBuilder {

    private final BranchRef branchRef;
    private final Matcher matcher;

    private PatchRefBuilder(BranchRef branchRef) {
      requireNonNull(branchRef);
      this.branchRef = branchRef;
      this.matcher = BRANCH_REF_CHANGE_PATTERN.matcher(branchRef.getBranchRefName().toString());
    }

    private Optional<PatchRef> build() {
      if (!matcher.find()) {
        return Optional.empty();
      }
      return Optional.of(
          new PatchRef(
              branchRef,
              ChangeNumericId.of(Integer.parseInt(matcher.group(1))),
              Integer.parseInt(matcher.group(2))));
    }
  }

  private class PatchRef {
    private final BranchRef branchRef;
    private final ChangeNumericId changeNumericId;
    private final int number;

    private PatchRef(BranchRef branchRef, ChangeNumericId changeNumericId, int number) {
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
