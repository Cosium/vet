package com.cosium.vet.command.push;

import com.cosium.vet.gerrit.CodeReviewVote;
import com.cosium.vet.gerrit.PatchsetSubject;

/**
 * Created on 23/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface PushCommandFactory {

  PushCommand build(
      Boolean publishDraftedComments,
      Boolean workInProgress,
      PatchsetSubject patchsetSubject,
      Boolean bypassReview,
      CodeReviewVote codeReviewVote);
}
