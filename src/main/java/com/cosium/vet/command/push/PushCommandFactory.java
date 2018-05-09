package com.cosium.vet.command.push;

import com.cosium.vet.gerrit.PatchSetSubject;

/**
 * Created on 23/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface PushCommandFactory {

  PushCommand build(
      Boolean publishDraftedComments,
      Boolean workInProgress,
      PatchSetSubject patchSetSubject,
      Boolean bypassReview);
}
