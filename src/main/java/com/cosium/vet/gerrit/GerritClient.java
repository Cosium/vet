package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchShortName;

import java.util.Optional;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GerritClient {

  /** @return The current change */
  Optional<GerritChange> getChange();

  /**
   * @param targetBranch The target branch
   * @param subject The change subject
   * @return The created change
   */
  GerritChange createAndSetChange(BranchShortName targetBranch, ChangeSubject subject);
}
