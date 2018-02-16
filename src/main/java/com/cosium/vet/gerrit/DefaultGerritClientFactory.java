package com.cosium.vet.gerrit;

import com.cosium.vet.git.GitRepositoryProvider;
import com.google.gerrit.extensions.api.GerritApi;

import static java.util.Objects.requireNonNull;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultGerritClientFactory implements GerritClientProvider {

  private final GitRepositoryProvider gitRepositoryProvider;

  public DefaultGerritClientFactory(GitRepositoryProvider gitRepositoryProvider) {
    requireNonNull(gitRepositoryProvider);
    this.gitRepositoryProvider = gitRepositoryProvider;
  }

  @Override
  public GerritApi getApi() {
    return null;
  }
}
