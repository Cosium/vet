package com.cosium.vet.command.push;

import com.cosium.vet.gerrit.CodeReviewVote;
import com.cosium.vet.gerrit.PatchSubject;

/**
 * Created on 23/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface PushCommandFactory {

  PushCommand build(
      Boolean publishDraftedComments,
      Boolean workInProgress,
      PatchSubject patchSetSubject,
      Boolean bypassReview,
      CodeReviewVote codeReviewVote);
}
