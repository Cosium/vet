package com.cosium.vet.gerrit;

/**
 * Created on 12/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface CreatedPatch extends Patch {

  /** @return The log produced while creating the patch */
  String getCreationLog();
}
