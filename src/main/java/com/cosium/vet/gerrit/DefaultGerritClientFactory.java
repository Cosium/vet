package com.cosium.vet.gerrit;

import com.cosium.vet.file.FileSystem;
import com.cosium.vet.gerrit.config.DefaultGerritConfigurationRepositoryFactory;
import com.cosium.vet.gerrit.config.GerritConfigurationRepository;
import com.cosium.vet.gerrit.config.GerritConfigurationRepositoryFactory;
import com.cosium.vet.git.GitClientFactory;
import com.cosium.vet.git.GitConfigRepositoryFactory;
import com.google.gerrit.extensions.api.GerritApi;
import com.urswolfer.gerrit.client.rest.GerritAuthData;
import com.urswolfer.gerrit.client.rest.GerritRestApiFactory;

import static java.util.Objects.requireNonNull;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultGerritClientFactory implements GerritClientFactory {

  private final GerritConfigurationRepositoryFactory configurationRepositoryFactory;
  private final GitClientFactory gitClientFactory;

  public DefaultGerritClientFactory(
      FileSystem fileSystem,
      GitConfigRepositoryFactory gitConfigRepositoryfactory,
      GitClientFactory gitClientFactory) {
    requireNonNull(gitClientFactory);
    this.configurationRepositoryFactory =
        new DefaultGerritConfigurationRepositoryFactory(fileSystem, gitConfigRepositoryfactory);
    this.gitClientFactory = gitClientFactory;
  }

  public DefaultGerritClientFactory(
      GerritConfigurationRepositoryFactory configurationRepositoryFactory,
      GitClientFactory gitClientFactory) {
    requireNonNull(configurationRepositoryFactory);
    requireNonNull(gitClientFactory);
    this.configurationRepositoryFactory = configurationRepositoryFactory;
    this.gitClientFactory = gitClientFactory;
  }

  @Override
  public GerritClient build() {
    GerritRestApiFactory gerritRestApiFactory = new GerritRestApiFactory();
    GerritAuthData.Basic authData =
        new GerritAuthData.Basic("http://localhost:8080", "user", "password");
    GerritApi gerritApi = gerritRestApiFactory.create(authData);

    GerritConfigurationRepository gerritConfigurationRepository =
        configurationRepositoryFactory.build();
    return new DefaultGerritClient(
        gerritConfigurationRepository, gerritApi, gitClientFactory.build());
  }
}
