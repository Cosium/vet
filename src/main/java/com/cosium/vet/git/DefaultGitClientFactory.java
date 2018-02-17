package com.cosium.vet.git;

import com.cosium.vet.App;
import com.cosium.vet.runtime.CommandRunner;
import org.apache.commons.lang3.BooleanUtils;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultGitClientFactory implements GitClientFactory {

  public static final String USE_DOCKER_GIT = App.NAME + ".use-docker-git";

  private final Path workingDirectory;
  private final GitExecutor gitExecutor;

  public DefaultGitClientFactory(Path workingDirectory, CommandRunner commandRunner) {
    requireNonNull(workingDirectory);
    requireNonNull(commandRunner);
    this.workingDirectory = workingDirectory;
    if (BooleanUtils.toBoolean(System.getProperty(USE_DOCKER_GIT))) {
      this.gitExecutor = new DockerGitExecutor(commandRunner);
    } else {
      this.gitExecutor = new BasicGitExecutor(commandRunner);
    }
  }

  @Override
  public GitClient buildClient() {
    return new DefaultGitClient(workingDirectory, gitExecutor);
  }
}
