package com.cosium.vet.gerrit;

import com.cosium.vet.git.*;
import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;
import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;

import java.util.Comparator;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
      GitClient gitClient,
      PushUrl pushUrl,
      PatchSetCommitMessageFactory commitMessageFactory) {
    this.git = requireNonNull(gitClient);
    this.pushUrl = requireNonNull(pushUrl);
    this.commitMessageFactory = requireNonNull(commitMessageFactory);
  }

  /**
   * @param pushUrl The gerrit push url
   * @param changeNumericId The targeted change numeric id
   * @return The latest revision for the provided change numeric id
   */
  private Optional<PatchSetRef> getLatestRevision(
          PushUrl pushUrl, ChangeNumericId changeNumericId) {
    return git.listRemoteRefs(RemoteName.of(pushUrl.toString()))
        .stream()
        .map(PatchSetRefBuilder::new)
        .map(PatchSetRefBuilder::build)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(patchSet -> patchSet.getChangeNumericId().equals(changeNumericId))
        .max(Comparator.comparingInt(PatchSetRef::getId));
  }

  @Override
  public Patch createPatch(
      BranchShortName targetBranch, ChangeNumericId numericId, String options) {
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
    CommitMessage latestPatchSetCommitMessage =
        getLastestPatchSetCommitMessage(numericId).orElse(null);
    CommitMessage commitMessage = commitMessageFactory.build(latestPatchSetCommitMessage);

    LOG.debug("Creating commit tree with message '{}'", commitMessage);
    String commitId = git.commitTree(endRevision, startRevision, commitMessage.toString());
    LOG.debug("Commit tree id is '{}'", commitId);

    LOG.debug("Pushing '{}' to '{}', with options '{}'", commitId, targetBranch, options);

    String output =
        git.push(
            pushUrl.toString(),
            String.format(
                "%s:refs/for/%s%%%s", commitId, targetBranch, StringUtils.defaultString(options)));
    return buildPatch(numericId, output);
  }

  private Patch buildPatch(ChangeNumericId numericId, String pushToRefForOutput) {
    numericId =
        ofNullable(numericId)
            .orElseGet(
                () -> ChangeNumericId.parseFromPushToRefForOutput(pushUrl, pushToRefForOutput));
    return new DefaultPatch(numericId);
  }

  @Override
  public Optional<CommitMessage> getLastestPatchSetCommitMessage(ChangeNumericId changeNumericId) {
    if (changeNumericId == null) {
      return Optional.empty();
    }

    PatchSetRef latestRevision = getLatestRevision(pushUrl, changeNumericId).orElse(null);
    if (latestRevision == null) {
      LOG.debug("No revision found for change {}", changeNumericId);
      return Optional.empty();
    }
    git.fetch(RemoteName.of(pushUrl.toString()), latestRevision.getBranchRefName());
    RevisionId revisionId = latestRevision.getRevisionId();
    return ofNullable(git.getCommitMessage(revisionId));
  }

  private class DefaultPatch implements Patch {
    private final ChangeNumericId changeNumericId;

    private DefaultPatch(ChangeNumericId changeNumericId) {
      this.changeNumericId = requireNonNull(changeNumericId);
    }

    @Override
    public ChangeNumericId getChangeNumericId() {
      return changeNumericId;
    }
  }

  private class PatchSetRefBuilder {

    private final BranchRef branchRef;
    private final Matcher matcher;

    private PatchSetRefBuilder(BranchRef branchRef) {
      requireNonNull(branchRef);
      this.branchRef = branchRef;
      this.matcher = BRANCH_REF_CHANGE_PATTERN.matcher(branchRef.getBranchRefName().toString());
    }

    private Optional<PatchSetRef> build() {
      if (!matcher.find()) {
        return Optional.empty();
      }
      return Optional.of(
          new PatchSetRef(
              branchRef,
              ChangeNumericId.of(Integer.parseInt(matcher.group(1))),
              Integer.parseInt(matcher.group(2))));
    }
  }

  private class PatchSetRef {
    private final BranchRef branchRef;
    private final ChangeNumericId changeNumericId;
    private final int id;

    private PatchSetRef(BranchRef branchRef, ChangeNumericId changeNumericId, int id) {
      requireNonNull(branchRef);
      this.branchRef = branchRef;
      this.changeNumericId = requireNonNull(changeNumericId);
      this.id = id;
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

    public int getId() {
      return id;
    }
  }
}
