package com.cosium.vet.git;

import com.cosium.vet.runtime.CommandRunner;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultGitClientFactory implements GitClientFactory {

  private final Path workingDirectory;
  private final GitExecutor gitExecutor;

  public DefaultGitClientFactory(
      Path workingDirectory, CommandRunner commandRunner, boolean useDockerGit) {
    requireNonNull(workingDirectory);
    requireNonNull(commandRunner);
    this.workingDirectory = workingDirectory;
    if (useDockerGit) {
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
