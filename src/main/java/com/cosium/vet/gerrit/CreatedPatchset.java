package com.cosium.vet.gerrit;

/**
 * Created on 12/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface CreatedPatchset extends Patchset {

  /** @return The log produced while creating the patchset */
  String getCreationLog();
}
