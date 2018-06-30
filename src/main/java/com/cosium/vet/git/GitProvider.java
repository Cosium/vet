package com.cosium.vet.git;

import com.cosium.vet.runtime.CommandRunner;
import com.cosium.vet.utils.OperatingSystem;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class GitProvider implements GitClientFactory, GitConfigRepositoryFactory {

  private final OperatingSystem operatingSystem;
  private final Path repositoryDirectory;
  private final CommandRunner commandRunner;

  public GitProvider(Path repositoryDirectory, CommandRunner commandRunner) {
    this(new OperatingSystem(), repositoryDirectory, commandRunner);
  }

  public GitProvider(
      OperatingSystem operatingSystem, Path repositoryDirectory, CommandRunner commandRunner) {
    this.operatingSystem = requireNonNull(operatingSystem);
    this.repositoryDirectory = requireNonNull(repositoryDirectory);
    this.commandRunner = requireNonNull(commandRunner);
  }

  @Override
  public GitClient build() {
    GitClient basicGitClient =
        new BasicGitClient(repositoryDirectory, commandRunner, buildRepository());
    if (!operatingSystem.isWindows()) {
      return basicGitClient;
    }
    return new WindowsGitClient(basicGitClient);
  }

  @Override
  public GitConfigRepository buildRepository() {
    return new DefaultGitConfigRepository(repositoryDirectory, commandRunner);
  }
}
