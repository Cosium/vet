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

  /**
   * Creates a patch set for the provided change between start revision and end revision
   *
   * @param change The change for which the patch set must be created
   * @param startRevision The start revision
   * @param endRevision The end revision
   * @param patchSetTitle The patch set title
   */
  void createPatchSet(
      GerritChange change, String startRevision, String endRevision, String patchSetTitle);
}
