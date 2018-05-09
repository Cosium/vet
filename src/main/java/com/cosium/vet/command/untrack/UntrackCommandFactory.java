package com.cosium.vet.command.untrack;

/**
 * Created on 09/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface UntrackCommandFactory {

  /**
   * @param force True to force the command execution without prompt
   * @return A new track command
   */
  UntrackCommand build(Boolean force);
}
