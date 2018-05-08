package com.cosium.vet.gerrit;

/**
 * Created on 23/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GerritChange {

  /**
   * Creates a patch set between start revision and end revision
   *
   * @param endRevision The end revision
   * @param publishDraftComments True to publish comments in draft. False otherwise.
   * @param workInProgress True to mark change set in work in progress
   * @param subject The patch set subject
   * @param bypassReview True to to bypass the review by directly submitting the change.
   */
  void createPatchSet(
      String endRevision,
      boolean publishDraftComments,
      boolean workInProgress,
      PatchSetSubject subject,
      boolean bypassReview);
}
