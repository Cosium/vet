package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchShortName;

import java.util.Optional;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface ChangeRepository {

  /** If a change is currently tracked, untrack it. Otherwise, does nothing */
  void untrack();

  /** @return The tracked change if any */
  Optional<Change> getTrackedChange();

  /**
   * @param numericId The change numeric ID
   * @param branchShortName The target branch short name
   * @return The tracked change
   */
  Change trackChange(ChangeNumericId numericId, BranchShortName branchShortName);

  /**
   * @param checkoutBranch The branch that will track the change
   * @param numericId The change numeric ID
   * @param branchShortName The target branch short name
   * @return The tracked change
   */
  Change checkoutAndTrackChange(
      ChangeCheckoutBranchName checkoutBranch,
      ChangeNumericId numericId,
      BranchShortName branchShortName);

  /**
   * Creates a new change on Gerrit and track it
   *
   * @param targetBranch The target branch
   * @param firstPatchOptions The options to apply to the first patch
   * @return The new tracked change
   */
  CreatedChange createAndTrackChange(BranchShortName targetBranch, PatchOptions firstPatchOptions);

  /**
   * Creates a new change
   *
   * @param targetBranch The target branch
   * @param firstPatchOptions The options to apply to the first patch
   * @return The new tracked change
   */
  CreatedChange createChange(BranchShortName targetBranch, PatchOptions firstPatchOptions);

  /**
   * @param numericId The change numeric id
   * @return True if a change with the provided numeric id exists
   */
  boolean exists(ChangeNumericId numericId);

  /**
   * Pull modifications from remote for the current tracked change
   *
   * @return The command output
   */
  String pull();
}
