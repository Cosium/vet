package com.cosium.vet.runtime;

/**
 * Created on 23/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface UserOutput {

  /**
   * @param message The message to display to the user
   * @param appendLineBreak Don't append a line break
   */
  void display(String message, boolean appendLineBreak);

  /** @param message The message to display to the user */
  void display(String message);
}
