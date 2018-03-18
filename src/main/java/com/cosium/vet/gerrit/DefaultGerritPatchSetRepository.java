package com.cosium.vet.gerrit;

import com.cosium.vet.git.*;
import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;

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
public class DefaultGerritPatchSetRepository implements GerritPatchSetRepository {

  private static final Pattern BRANCH_REF_CHANGE_PATTERN =
      Pattern.compile("refs/changes/\\d{2}/(\\d+)/(\\d+)");

  private final GitClient git;

  public DefaultGerritPatchSetRepository(GitClient gitClient) {
    requireNonNull(gitClient);
    this.git = gitClient;
  }

  /** @return The latest revisions per change id */
  private List<PatchSet> getLatestRevisions(GerritPushUrl pushUrl) {
    return git.listRemoteRefs(RemoteName.of(pushUrl.toString()))
        .stream()
        .map(PatchSetBuilder::new)
        .map(PatchSetBuilder::build)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(
            Collectors.groupingBy(
                PatchSet::getChangeId, Collectors.maxBy(Comparator.comparingInt(PatchSet::getId))))
        .values()
        .stream()
        .filter(Optional::isPresent)
        .map(Optional::get)
        .sorted(Comparator.comparingInt(PatchSet::getChangeId).reversed())
        .collect(Collectors.toList());
  }

  @Override
  public Optional<CommitMessage> getLastestPatchSetCommitMessage(
      GerritPushUrl pushUrl, ChangeChangeId changeChangeId) {
    return getLatestRevisions(pushUrl)
        .parallelStream()
        .peek(patchSet -> git.fetch(RemoteName.of(pushUrl.toString()), patchSet.getBranchRefName()))
        .map(PatchSet::getRevisionId)
        .map(git::getCommitMessage)
        .filter(
            commitMessage ->
                StringUtils.contains(commitMessage.toString(), changeChangeId.toString()))
        .findFirst();
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
              branchRef, Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2))));
    }
  }

  private class PatchSet {
    private final BranchRef branchRef;
    private final int changeId;
    private final int id;

    private PatchSet(BranchRef branchRef, int changeId, int id) {
      requireNonNull(branchRef);
      this.branchRef = branchRef;
      this.changeId = changeId;
      this.id = id;
    }

    public BranchRefName getBranchRefName() {
      return branchRef.getBranchRefName();
    }

    public RevisionId getRevisionId() {
      return branchRef.getRevisionId();
    }

    public int getId() {
      return id;
    }

    public int getChangeId() {
      return changeId;
    }
  }
}
