package com.cosium.vet.gerrit;

import com.cosium.vet.git.RevisionId;

/**
 * Created on 23/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface Change {

  /** @return The numeric id of this change */
  ChangeNumericId getNumericId();

  /** @return The parent revision of this change */
  RevisionId fetchParent();

  /**
   * Creates a patch set between start revision and end revision
   *
   * @param publishDraftComments True to publish comments in draft. False otherwise.
   * @param workInProgress True to mark change set in work in progress
   * @param subject The patch set subject
   * @param bypassReview True to to bypass the review by directly submitting the change.
   * @return The creationg log output
   */
  String createPatchSet(
      boolean publishDraftComments,
      boolean workInProgress,
      PatchSetSubject subject,
      boolean bypassReview,
      CodeReviewVote codeReviewVote);
}
