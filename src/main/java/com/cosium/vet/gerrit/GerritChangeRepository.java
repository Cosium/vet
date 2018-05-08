package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchShortName;

import java.util.Optional;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GerritChangeRepository {

  Optional<GerritChange> getTrackedChange();

  /**
   * @param numericId The change numeric ID
   * @param branchShortName The target branch short name
   * @return The tracked change
   */
  GerritChange trackChange(ChangeNumericId numericId, BranchShortName branchShortName);
}
