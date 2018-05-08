package com.cosium.vet.gerrit;

import com.cosium.vet.git.*;
import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;
import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;
import com.cosium.vet.utils.NonBlankString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class DefaultGerritChange implements GerritChange {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultGerritChange.class);

  private final GitClient git;
  private final PatchSetCommitMessageFactory patchSetCommitMessageFactory;

  private final GerritPushUrl pushUrl;
  private final ChangeNumericId numericId;
  private final BranchShortName targetBranch;

  private DefaultGerritChange(
      GitClient git,
      PatchSetCommitMessageFactory patchSetCommitMessageFactory,
      GerritPushUrl pushUrl,
      ChangeNumericId numericId,
      BranchShortName targetBranch) {
    this.git = requireNonNull(git);
    this.patchSetCommitMessageFactory = requireNonNull(patchSetCommitMessageFactory);

    this.pushUrl = requireNonNull(pushUrl);
    this.numericId = requireNonNull(numericId);
    this.targetBranch = requireNonNull(targetBranch);
  }

  @Override
  public void createPatchSet(
      String endRevision,
      boolean publishDraftComments,
      boolean workInProgress,
      PatchSetSubject subject,
      boolean bypassReview) {

    RemoteName remote =
        git.getRemote(targetBranch)
            .orElseThrow(
                () ->
                    new RuntimeException(
                        String.format("No remote found for branch '%s'", targetBranch)));
    git.fetch(remote, targetBranch);
    String startRevision =
        git.getMostRecentCommonCommit(String.format("%s/%s", remote, targetBranch));

    LOG.debug(
        "Creating patch set for change '{}' between start revision '{}' and end revision '{}'",
        this,
        startRevision,
        endRevision);
    CommitMessage commitMessage = patchSetCommitMessageFactory.build(numericId);

    LOG.debug("Creating commit tree with message '{}'", commitMessage);
    String commitId = git.commitTree(endRevision, startRevision, commitMessage.toString());
    LOG.debug("Commit tree id is '{}'", commitId);

    String options =
        buildPatchSetOptions(publishDraftComments, workInProgress, subject, bypassReview);
    LOG.debug("Pushing '{}' to '{}', with options '{}'", commitId, targetBranch, options);

    git.push(
        pushUrl.toString(), String.format("%s:refs/for/%s%%%s", commitId, targetBranch, options));
  }

  private String buildPatchSetOptions(
      boolean publishDraftedComments,
      boolean workInProgress,
      PatchSetSubject subject,
      boolean bypassReview) {
    List<String> options = new ArrayList<>();
    options.add(publishDraftedComments ? "publish-comments" : null);
    options.add(workInProgress ? "wip" : null);
    options.add(
        ofNullable(subject)
            .map(NonBlankString::toString)
            .map(GitUtils::encodeForGitRef)
            .map(s -> String.format("m=%s", s))
            .orElse(null));
    options.add(bypassReview ? "submit" : null);
    return options.stream().filter(StringUtils::isNotBlank).collect(Collectors.joining(","));
  }

  /**
   * Created on 08/05/18.
   *
   * @author Reda.Housni-Alaoui
   */
  static class Factory implements GerritChangeFactory {

    private final GitClient git;
    private final PatchSetCommitMessageFactory patchSetCommitMessageFactory;
    private final GerritPushUrl pushUrl;

    Factory(
        GitClient git,
        PatchSetCommitMessageFactory patchSetCommitMessageFactory,
        GerritPushUrl pushUrl) {
      this.git = requireNonNull(git);
      this.patchSetCommitMessageFactory = requireNonNull(patchSetCommitMessageFactory);
      this.pushUrl = requireNonNull(pushUrl);
    }

    @Override
    public GerritChange build(ChangeNumericId changeNumericId, BranchShortName targetBranch) {
      return new DefaultGerritChange(
          git, patchSetCommitMessageFactory, pushUrl, changeNumericId, targetBranch);
    }
  }

  @Override
  public String toString() {
    return "{\"numericId\": " + numericId + ", \"targetBranch\": \"" + targetBranch + "\"}";
  }
}
