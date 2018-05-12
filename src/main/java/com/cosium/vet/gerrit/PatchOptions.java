package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitUtils;
import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;
import com.cosium.vet.utils.NonBlankString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

/**
 * Created on 12/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class PatchOptions {

  public static final PatchOptions DEFAULT = PatchOptions.builder().build();

  private final boolean publishDraftComments;
  private final boolean workInProgress;
  private final PatchSubject subject;
  private final boolean bypassReview;
  private final CodeReviewVote codeReviewVote;

  private PatchOptions(
      boolean publishDraftComments,
      boolean workInProgress,
      PatchSubject subject,
      boolean bypassReview,
      CodeReviewVote codeReviewVote) {
    this.publishDraftComments = publishDraftComments;
    this.workInProgress = workInProgress;
    this.subject = subject;
    this.bypassReview = bypassReview;
    this.codeReviewVote = codeReviewVote;
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * @param revisionId The revision to push
   * @param targetBranch The target branch of the change
   * @return The target to push to
   */
  public String buildGitPushTarget(String revisionId, BranchShortName targetBranch) {
    List<String> options = new ArrayList<>();
    options.add(publishDraftComments ? "publish-comments" : null);
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

    String rawOptions =
        options.stream().filter(StringUtils::isNotBlank).collect(Collectors.joining(","));

    return String.format("%s:refs/for/%s%%%s", revisionId, targetBranch, rawOptions);
  }

  public static class Builder {
    private boolean publishDraftComments;
    private boolean workInProgress;
    private PatchSubject subject;
    private boolean bypassReview;
    private CodeReviewVote codeReviewVote;

    private Builder() {}

    public Builder publishDraftComments() {
      return publishDraftComments(true);
    }

    public Builder publishDraftComments(boolean publishDraftComments) {
      this.publishDraftComments = publishDraftComments;
      return this;
    }

    public Builder workInProgress() {
      return workInProgress(true);
    }

    public Builder workInProgress(boolean workInProgress) {
      this.workInProgress = workInProgress;
      return this;
    }

    public Builder subject(PatchSubject subject) {
      this.subject = subject;
      return this;
    }

    public Builder bypassReview() {
      return bypassReview(true);
    }

    public Builder bypassReview(boolean bypassReview) {
      this.bypassReview = bypassReview;
      return this;
    }

    public Builder codeReviewVote(CodeReviewVote codeReviewVote) {
      this.codeReviewVote = codeReviewVote;
      return this;
    }

    public PatchOptions build() {
      return new PatchOptions(
          publishDraftComments, workInProgress, subject, bypassReview, codeReviewVote);
    }
  }
}
