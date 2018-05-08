package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchShortName;

import java.util.Optional;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GerritChangeRepository {

  /** If a change is currently tracked, untrack it. Otherwise, does nothing */
  void untrack();

  /** @return The tracked change if any */
  Optional<GerritChange> getTrackedChange();

  /**
   * @param numericId The change numeric ID
   * @param branchShortName The target branch short name
   * @return The tracked change
   */
  GerritChange trackChange(ChangeNumericId numericId, BranchShortName branchShortName);

  /**
   * @param numericId The change numeric id
   * @return True if a change with the provided numeric id exists
   */
  boolean exists(ChangeNumericId numericId);
}
