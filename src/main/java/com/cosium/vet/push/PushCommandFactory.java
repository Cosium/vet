package com.cosium.vet.push;

import com.cosium.vet.gerrit.PatchSetSubject;
import com.cosium.vet.git.BranchShortName;

/**
 * Created on 23/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface PushCommandFactory {

  PushCommand build(
      BranchShortName targetBranch,
      Boolean publishDraftedComments,
      Boolean workInProgress,
      PatchSetSubject patchSetSubject,
      Boolean bypassReview);
}
