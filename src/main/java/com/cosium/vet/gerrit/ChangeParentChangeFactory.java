package com.cosium.vet.gerrit;

/**
 * Created on 01/08/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface ChangeParentChangeFactory {

  /**
   * @param parentChangeNumericId The numeric id of the parent change
   * @return A change parent represented by the provided change
   */
  ChangeParent build(ChangeNumericId parentChangeNumericId);
}
