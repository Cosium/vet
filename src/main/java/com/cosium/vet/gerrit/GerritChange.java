package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchShortName;

/**
 * Created on 23/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GerritChange {
  /** @return The target branch of the change */
  BranchShortName getTargetBranch();
}
