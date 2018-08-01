package com.cosium.vet.gerrit;

/**
 * Created on 01/08/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface AlterableChange extends Change {

  /**
   * Creates a patchset based on the diff between the HEAD and target branch
   *
   * @param options The patchset options
   * @return The creationg log output
   */
  String createPatchset(PatchsetOptions options);
}
