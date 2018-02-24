package com.cosium.vet;

import com.cosium.vet.runtime.BasicCommandRunner;
import com.cosium.vet.runtime.CommandRunner;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * Created on 17/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class TestCommandRunner implements CommandRunner {

  private static final Logger LOG = LoggerFactory.getLogger(TestCommandRunner.class);

  private static final String DOCKER_CMD = "docker";
  private static final String DOCKER_GIT_IMAGE = "alpine/git:1.0.4";

  private static Boolean gitAvailable;

  private final CommandRunner delegate;

  public TestCommandRunner() {
    this.delegate = new BasicCommandRunner();
  }

  @Override
  public String run(Path workingDir, String... command) {
    if ("git".equalsIgnoreCase(command[0])) {
      if (gitAvailable == null) {
        try {
          delegate.run(workingDir, "git", "--version");
          LOG.info("git is available");
          gitAvailable = true;
        } catch (Throwable t) {
          LOG.info("git is not available. Using docker image.");
          gitAvailable = false;
        }
      }

      if (!gitAvailable) {
        String uid;
        try {
          uid = delegate.run(workingDir, "id", "-u", System.getProperty("user.name"));
        } catch (Throwable t) {
          uid = null;
        }

        String[] gitBaseCommand = {
          DOCKER_CMD, "run", "--rm", "-v", String.format("%s:/git", workingDir), "--net", "host"
        };

        if (StringUtils.isNotBlank(uid)) {
          gitBaseCommand = ArrayUtils.addAll(gitBaseCommand, "--user", uid);
        }
        gitBaseCommand = ArrayUtils.add(gitBaseCommand, DOCKER_GIT_IMAGE);
        command = ArrayUtils.addAll(gitBaseCommand, ArrayUtils.remove(command, 0));
      }
    }
    return delegate.run(workingDir, command);
  }
}
