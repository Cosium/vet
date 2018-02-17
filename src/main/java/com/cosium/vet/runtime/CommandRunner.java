package com.cosium.vet.runtime;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface CommandRunner {

  /**
   * @param command The command to execute
   * @return The command output
   */
  String run(String... command);
}
