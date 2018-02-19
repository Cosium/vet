package com.cosium.vet.git;

import com.cosium.vet.runtime.CommandRunner;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class GitProvider implements GitClientFactory, GitConfigRepositoryProvider {

  private final Path workingDirectory;
  private final CommandRunner commandRunner;

  public GitProvider(Path workingDirectory, CommandRunner commandRunner) {
    requireNonNull(workingDirectory);
    requireNonNull(commandRunner);
    this.workingDirectory = workingDirectory;
    this.commandRunner = commandRunner;
  }

  @Override
  public GitClient buildClient() {
    return new DefaultGitClient(workingDirectory, commandRunner);
  }

  @Override
  public GitConfigRepository getRepository() {
    return new DefaultGitConfigRepository(workingDirectory, commandRunner, buildClient());
  }
}
