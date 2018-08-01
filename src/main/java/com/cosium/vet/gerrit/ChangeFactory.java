package com.cosium.vet.gerrit;

/**
 * Created on 01/08/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface ChangeFactory {

  /**
   * @param changeNumericId The numeric id of the change
   * @return The change
   */
  Change build(ChangeNumericId changeNumericId);
}
