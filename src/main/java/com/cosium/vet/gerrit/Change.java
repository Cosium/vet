package com.cosium.vet.gerrit;

import com.cosium.vet.git.RevisionId;

/**
 * Created on 23/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface Change {

  /** @return The numeric id of this change */
  ChangeNumericId getNumericId();

  /** @return The parent revision of this change */
  RevisionId fetchParent();

  /**
   * Creates a patchset based on the diff between the HEAD and target branch
   *
   * @param options The patchset options
   * @return The creationg log output
   */
  String createPatchset(PatchsetOptions options);

  /** @return The change web url */
  String getWebUrl();
}
