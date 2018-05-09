package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.CommitMessage;

import java.util.Optional;

/**
 * Created on 27/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface PatchSetRepository {

  /**
   * @param targetBranch The target of the change
   * @param numericId The numeric id of the change. Can be null.
   * @param options The options for the push. Can be null.
   * @return The created patch
   */
  Patch createPatch(BranchShortName targetBranch, ChangeNumericId numericId, String options);

  /**
   * @param changeNumericId The change numeric id
   * @return The latest patch set commit message of the provided change numeric id.
   */
  Optional<CommitMessage> getLastestPatchSetCommitMessage(ChangeNumericId changeNumericId);
}
