package com.cosium.vet;

import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;
import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created on 25/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class ProcessUtils {

  private static final Logger LOG = LoggerFactory.getLogger(ProcessUtils.class);

  public static Process create(Path workingDir, String... command) {
    try {
      ProcessBuilder processBuilder =
          new ProcessBuilder(command)
              .directory(workingDir.toFile())
              .redirectInput(ProcessBuilder.Redirect.INHERIT)
              .redirectError(ProcessBuilder.Redirect.INHERIT);

      LOG.debug("Executing '{}'", StringUtils.join(command, StringUtils.SPACE));
      return processBuilder.start();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
