package com.cosium.vet.gerrit;

import com.cosium.vet.git.*;
import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;

import java.util.Comparator;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/**
 * Created on 27/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultGerritPatchSetRepository implements GerritPatchSetRepository {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultGerritPatchSetRepository.class);

  private static final Pattern BRANCH_REF_CHANGE_PATTERN =
      Pattern.compile("refs/changes/\\d{2}/(\\d+)/(\\d+)");

  private final GitClient git;
  private final GerritPushUrl pushUrl;

  public DefaultGerritPatchSetRepository(GitClient gitClient, GerritPushUrl pushUrl) {
    this.git = requireNonNull(gitClient);
    this.pushUrl = requireNonNull(pushUrl);
  }

  /**
   * @param pushUrl The gerrit push url
   * @param changeNumericId The targeted change numeric id
   * @return The latest revision for the provided change numeric id
   */
  private Optional<PatchSet> getLatestRevision(
      GerritPushUrl pushUrl, ChangeNumericId changeNumericId) {
    return git.listRemoteRefs(RemoteName.of(pushUrl.toString()))
        .stream()
        .map(PatchSetBuilder::new)
        .map(PatchSetBuilder::build)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(patchSet -> patchSet.getChangeNumericId().equals(changeNumericId))
        .max(Comparator.comparingInt(PatchSet::getId));
  }

  @Override
  public Optional<CommitMessage> getLastestPatchSetCommitMessage(ChangeNumericId changeNumericId) {
    PatchSet latestRevision = getLatestRevision(pushUrl, changeNumericId).orElse(null);
    if (latestRevision == null) {
      LOG.debug("No revision found for change {}", changeNumericId);
      return Optional.empty();
    }
    git.fetch(RemoteName.of(pushUrl.toString()), latestRevision.getBranchRefName());
    RevisionId revisionId = latestRevision.getRevisionId();
    return Optional.ofNullable(git.getCommitMessage(revisionId));
  }

  private class PatchSetBuilder {

    private final BranchRef branchRef;
    private final Matcher matcher;

    private PatchSetBuilder(BranchRef branchRef) {
      requireNonNull(branchRef);
      this.branchRef = branchRef;
      this.matcher = BRANCH_REF_CHANGE_PATTERN.matcher(branchRef.getBranchRefName().toString());
    }

    private Optional<PatchSet> build() {
      if (!matcher.find()) {
        return Optional.empty();
      }
      return Optional.of(
          new PatchSet(
              branchRef,
              ChangeNumericId.of(Integer.parseInt(matcher.group(1))),
              Integer.parseInt(matcher.group(2))));
    }
  }

  private class PatchSet {
    private final BranchRef branchRef;
    private final ChangeNumericId changeNumericId;
    private final int id;

    private PatchSet(BranchRef branchRef, ChangeNumericId changeNumericId, int id) {
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
