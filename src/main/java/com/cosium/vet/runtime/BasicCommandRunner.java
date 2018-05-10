package com.cosium.vet.runtime;

import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;
import com.cosium.vet.thirdparty.apache_commons_io.IOUtils;
import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class BasicCommandRunner implements CommandRunner {

  private static final Logger LOG = LoggerFactory.getLogger(BasicCommandRunner.class);

  @Override
  public String run(Path workingDir, String... command) {
    try {
      ProcessBuilder processBuilder =
          new ProcessBuilder(command)
              .directory(workingDir.toFile())
              .redirectInput(ProcessBuilder.Redirect.INHERIT);

      LOG.debug("Executing '{}'", StringUtils.join(command, StringUtils.SPACE));
      Process process = processBuilder.start();

      String output =
          IOUtils.toString(process.getInputStream(), "UTF-8").trim()
              + IOUtils.toString(process.getErrorStream(), "UTF-8").trim();

      int exitCode = process.waitFor();
      if (exitCode != 0) {
        throw new CommandRunException(exitCode, output, command);
      }

      return StringUtils.defaultIfBlank(output, null);
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
