package com.cosium.vet.runtime;

import java.nio.file.Path;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface CommandRunner {

  /**
   * @param workingDir The working directory
   * @param command The command to execute
   * @return The command output
   */
  String run(Path workingDir, String... command);

}
