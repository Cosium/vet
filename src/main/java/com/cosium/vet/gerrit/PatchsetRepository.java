package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchShortName;

import java.util.Optional;

/**
 * Created on 27/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface PatchsetRepository {

  /**
   * @param targetBranch The target of the change
   * @param options The options for the push.
   * @return The created patchset
   */
  CreatedPatchset createPatchset(BranchShortName targetBranch, PatchsetOptions options);

  /**
   * @param targetBranch The target of the change
   * @param numericId The numeric id of the change. Can be null.
   * @param options The options for the push.
   * @return The created patchset
   */
  CreatedPatchset createPatchset(
      BranchShortName targetBranch, ChangeNumericId numericId, PatchsetOptions options);

  /**
   * @param changeNumericId The change numeric id
   * @return The latest patchset of the provided change numeric id.
   */
  Optional<Patchset> findLastestPatchset(ChangeNumericId changeNumericId);

  /**
   * @param changeNumericId The change numeric id
   * @param patchsetNumber The patchset number. Incremental from 1.
   * @return The patchset matching the provided parameters.
   */
  Patchset findPatchset(ChangeNumericId changeNumericId, int patchsetNumber);

  /**
   * @param changeNumericId The numeric id of the change to pull
   * @return The command output
   */
  String pullLatestPatchset(ChangeNumericId changeNumericId);
}
