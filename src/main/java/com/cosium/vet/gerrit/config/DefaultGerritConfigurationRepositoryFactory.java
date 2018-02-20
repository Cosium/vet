package com.cosium.vet.gerrit.config;

import com.cosium.vet.file.FileSystem;
import com.cosium.vet.git.GitConfigRepositoryFactory;

import static java.util.Objects.requireNonNull;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultGerritConfigurationRepositoryFactory implements GerritConfigurationRepositoryFactory {

  private final FileSystem fileSystem;
  private final GitConfigRepositoryFactory gitConfigRepositoryFactory;

  public DefaultGerritConfigurationRepositoryFactory(
      FileSystem fileSystem, GitConfigRepositoryFactory gitConfigRepositoryFactory) {
    requireNonNull(fileSystem);
    requireNonNull(gitConfigRepositoryFactory);
    this.fileSystem = fileSystem;
    this.gitConfigRepositoryFactory = gitConfigRepositoryFactory;
  }

  @Override
  public GerritConfigurationRepository build() {
    return new MixedGerritConfigurationRepository(
        fileSystem, gitConfigRepositoryFactory.buildRepository());
  }
}
