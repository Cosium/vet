package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitUtils;
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
class DefaultChange implements Change {

  private final PatchSetRepository patchSetRepository;
  private final PushUrl pushUrl;
  private final BranchShortName targetBranch;
  private final ChangeNumericId numericId;

  private DefaultChange(
      PatchSetRepository patchSetRepository,
      PushUrl pushUrl,
      BranchShortName targetBranch,
      ChangeNumericId numericId) {
    this.patchSetRepository = requireNonNull(patchSetRepository);
    this.pushUrl = requireNonNull(pushUrl);
    this.targetBranch = requireNonNull(targetBranch);
    this.numericId = requireNonNull(numericId);
  }

  @Override
  public ChangeNumericId getNumericId() {
    return numericId;
  }

  @Override
  public String createPatchSet(
      boolean publishDraftComments,
      boolean workInProgress,
      PatchSetSubject subject,
      boolean bypassReview,
      CodeReviewVote codeReviewVote) {
    Patch patch =
        patchSetRepository.createPatch(
            targetBranch,
            numericId,
            buildPatchSetOptions(
                publishDraftComments, workInProgress, subject, bypassReview, codeReviewVote));
    return patch
        .getCreationLog()
        .orElseThrow(() -> new RuntimeException("Could not extract the creation log"));
  }

  private String buildPatchSetOptions(
      boolean publishDraftedComments,
      boolean workInProgress,
      PatchSetSubject subject,
      boolean bypassReview,
      CodeReviewVote codeReviewVote) {
    List<String> options = new ArrayList<>();
    options.add(publishDraftedComments ? "publish-comments" : null);
    options.add(workInProgress ? "wip" : null);
    options.add(
        ofNullable(subject)
            .map(NonBlankString::toString)
            .map(GitUtils::encodeForGitRef)
            .map(s -> String.format("m=%s", s))
            .orElse(null));
    options.add(
        ofNullable(codeReviewVote)
            .map(CodeReviewVote::toString)
            .map(vote -> "Code-Review" + vote)
            .map(code -> String.format("l=%s", code))
            .orElse(null));
    options.add(bypassReview ? "submit" : null);
    return options.stream().filter(StringUtils::isNotBlank).collect(Collectors.joining(","));
  }

  @Override
  public String toString() {
    return pushUrl.computeChangeWebUrl(numericId) + " ";
  }

  /**
   * Created on 08/05/18.
   *
   * @author Reda.Housni-Alaoui
   */
  static class Factory implements ChangeFactory {

    private final PatchSetRepository patchSetRepository;
    private final PushUrl pushUrl;

    Factory(PatchSetRepository patchSetRepository, PushUrl pushUrl) {
      this.patchSetRepository = requireNonNull(patchSetRepository);
      this.pushUrl = requireNonNull(pushUrl);
    }

    @Override
    public Change build(BranchShortName targetBranch, ChangeNumericId changeNumericId) {
      return new DefaultChange(patchSetRepository, pushUrl, targetBranch, changeNumericId);
    }
  }
}
