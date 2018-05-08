package com.cosium.vet.gerrit.config;

import com.cosium.vet.gerrit.ChangeNumericId;
import com.cosium.vet.git.BranchShortName;

import java.util.Optional;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GerritConfiguration {

  /** @return The current Gerrit change numeric ID */
  Optional<ChangeNumericId> getTrackedChangeNumericId();

  /** Sets the current Gerrit change numeric ID */
  void setTrackedChangeNumericId(ChangeNumericId changeNumericId);

  /** @return The current Gerrit tracked change target branch */
  Optional<BranchShortName> getTrackedChangeTargetBranch();

  /** Sets the current Gerrit change target branch */
  void setTrackedChangeTargetBranch(BranchShortName targetBranch);
}
