package com.cosium.vet.git;

import com.cosium.vet.runtime.CommandRunner;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Paths;

import static java.util.Objects.requireNonNull;

/**
 * Created on 17/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DockerGitExecutor implements GitExecutor {

  private static final String DOCKER_CMD = "docker";
  private static final String DOCKER_GIT_IMAGE = "alpine/git:1.0.4";

  private final CommandRunner commandRunner;

  public DockerGitExecutor(CommandRunner commandRunner) {
    requireNonNull(commandRunner);
    this.commandRunner = commandRunner;
  }

  @Override
  public String execute(String... arguments) {
    String[] baseCommand = {
      DOCKER_CMD,
      "run",
      "-t",
      "--rm",
      "-v",
      "${HOME}:/root",
      String.format("%s:/git", getCurrentPath()),
      DOCKER_GIT_IMAGE
    };
    return commandRunner.run(ArrayUtils.addAll(baseCommand, arguments));
  }

  private String getCurrentPath() {
    return Paths.get(StringUtils.EMPTY).toAbsolutePath().toString();
  }
}
