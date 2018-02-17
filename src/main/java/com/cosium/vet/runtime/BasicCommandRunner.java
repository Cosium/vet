package com.cosium.vet.runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class BasicCommandRunner implements CommandRunner {
  @Override
  public String run(String... command) {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(command);
      processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);
      processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

      Process process = processBuilder.start();
      int exitCode = process.waitFor();
      if (exitCode != 0) {
        throw new RuntimeException(
            String.format("'%s' failed with code %s", Arrays.toString(command), exitCode));
      }

      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        return reader.readLine();
      }
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
