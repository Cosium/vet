package com.cosium.vet.gerrit;

import com.cosium.vet.git.GitClientFactory;
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

  private final GitClientFactory gitClientFactory;

  public DefaultGerritClientFactory(GitClientFactory gitClientFactory) {
    requireNonNull(gitClientFactory);
    this.gitClientFactory = gitClientFactory;
  }

  @Override
  public GerritClient buildClient() {
    GerritRestApiFactory gerritRestApiFactory = new GerritRestApiFactory();
    GerritAuthData.Basic authData =
        new GerritAuthData.Basic("http://localhost:8080", "user", "password");
    GerritApi gerritApi = gerritRestApiFactory.create(authData);
    return new DefaultGerritClient(gerritApi);
  }
}
