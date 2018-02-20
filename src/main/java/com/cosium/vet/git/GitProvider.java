package com.cosium.vet.git;

import com.cosium.vet.runtime.CommandRunner;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class GitProvider implements GitClientFactory, GitConfigRepositoryFactory {

  private final Path repositoryDirectory;
  private final CommandRunner commandRunner;

  public GitProvider(Path repositoryDirectory, CommandRunner commandRunner) {
    requireNonNull(repositoryDirectory);
    requireNonNull(commandRunner);
    this.repositoryDirectory = repositoryDirectory;
    this.commandRunner = commandRunner;
  }

  @Override
  public GitClient build() {
    return new DefaultGitClient(repositoryDirectory, commandRunner, buildRepository());
  }

  @Override
  public GitConfigRepository buildRepository() {
    return new DefaultGitConfigRepository(repositoryDirectory, commandRunner);
  }
}
