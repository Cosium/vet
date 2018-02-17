package com.cosium.vet.git;

import com.cosium.vet.runtime.CommandRunner;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.file.Path;

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
  public String execute(Path workingDir, String... arguments) {
    String[] baseCommand = {
      DOCKER_CMD, "run", "-t", "--rm", "-v", String.format("%s:/git", workingDir), DOCKER_GIT_IMAGE
    };
    return commandRunner.run(workingDir, ArrayUtils.addAll(baseCommand, arguments));
  }
}
