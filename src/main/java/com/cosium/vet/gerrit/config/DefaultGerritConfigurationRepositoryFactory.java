package com.cosium.vet.gerrit.config;

import com.cosium.vet.git.GitConfigRepositoryFactory;

import static java.util.Objects.requireNonNull;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultGerritConfigurationRepositoryFactory
    implements GerritConfigurationRepositoryFactory {

  private final GitConfigRepositoryFactory gitConfigRepositoryFactory;

  public DefaultGerritConfigurationRepositoryFactory(
      GitConfigRepositoryFactory gitConfigRepositoryFactory) {
    requireNonNull(gitConfigRepositoryFactory);
    this.gitConfigRepositoryFactory = gitConfigRepositoryFactory;
  }

  @Override
  public GerritConfigurationRepository build() {
    return new DefaultGerritConfigurationRepository(gitConfigRepositoryFactory.buildRepository());
  }
}
