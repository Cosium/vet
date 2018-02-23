package com.cosium.vet.gerrit.config;

import com.cosium.vet.git.BranchShortName;

import java.util.Optional;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GerritConfiguration {

  /** @return The current Gerrit change target branch */
  Optional<BranchShortName> getChangeTargetBranch();

  /** Sets the current Gerrit target branch */
  void setChangeTargetBranch(BranchShortName targetBranch);
}
