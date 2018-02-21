package com.cosium.vet.gerrit;

import com.cosium.vet.file.FileSystem;
import com.cosium.vet.gerrit.config.DefaultGerritConfigurationRepositoryFactory;
import com.cosium.vet.gerrit.config.GerritConfigurationRepository;
import com.cosium.vet.gerrit.config.GerritConfigurationRepositoryFactory;
import com.cosium.vet.gerrit.config.GerritSiteConfiguration;
import com.cosium.vet.git.*;
import com.cosium.vet.runtime.UserInput;
import com.google.gerrit.extensions.api.GerritApi;
import com.urswolfer.gerrit.client.rest.GerritAuthData;
import com.urswolfer.gerrit.client.rest.GerritRestApiFactory;

import java.net.URL;

import static java.util.Objects.requireNonNull;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultGerritClientFactory implements GerritClientFactory {

  private final GerritConfigurationRepositoryFactory configurationRepositoryFactory;
  private final GitClientFactory gitClientFactory;
  private final GerritRestApiFactory gerritRestApiFactory;
  private final UserInput userInput;

  public DefaultGerritClientFactory(
      FileSystem fileSystem,
      GitConfigRepositoryFactory gitConfigRepositoryfactory,
      GitClientFactory gitClientFactory,
      UserInput userInput) {
    this(
        new DefaultGerritConfigurationRepositoryFactory(fileSystem, gitConfigRepositoryfactory),
        gitClientFactory,
        new GerritRestApiFactory(),
        userInput);
  }

  public DefaultGerritClientFactory(
      GerritConfigurationRepositoryFactory configurationRepositoryFactory,
      GitClientFactory gitClientFactory,
      GerritRestApiFactory gerritRestApiFactory,
      UserInput userInput) {
    requireNonNull(configurationRepositoryFactory);
    requireNonNull(gitClientFactory);
    requireNonNull(gerritRestApiFactory);
    requireNonNull(userInput);
    this.configurationRepositoryFactory = configurationRepositoryFactory;
    this.gitClientFactory = gitClientFactory;
    this.gerritRestApiFactory = gerritRestApiFactory;
    this.userInput = userInput;
  }

  @Override
  public GerritClient build() {
    GitClient git = gitClientFactory.build();
    RemoteName remote = RemoteName.ORIGIN;
    URL remoteUrl =
        git.getRemoteUrl(remote)
            .map(RemoteUrl::toURL)
            .orElseThrow(
                () ->
                    new RuntimeException(
                        String.format("Could not find url of remote '%s'", remote)));

    GerritPushUrl pushUrl = GerritPushUrl.of(remoteUrl.toString());
    GerritHttpRootUrl rootUrl = pushUrl.parseHttpRootUrl();
    GerritProjectName project = pushUrl.parseProjectName();

    GerritSiteConfiguration siteConf = getOrCreateSiteConfiguration(rootUrl);

    GerritAuthData.Basic authData =
        new GerritAuthData.Basic(
            siteConf.getHttpUrl(), siteConf.getHttpLogin(), siteConf.getHttpPassword());
    GerritApi gerritApi = gerritRestApiFactory.create(authData);

    GerritConfigurationRepository gerritConfigurationRepository =
        configurationRepositoryFactory.build();
    return new DefaultGerritClient(gerritConfigurationRepository, gerritApi, pushUrl, project);
  }

  /**
   * @param rootUrl The site root url
   * @return The created or found site configuration
   */
  private GerritSiteConfiguration getOrCreateSiteConfiguration(GerritHttpRootUrl rootUrl) {
    return configurationRepositoryFactory
        .build()
        .readAndWrite(
            conf ->
                conf.getSite(rootUrl)
                    .orElseGet(
                        () -> {
                          String user = userInput.askNonBlank("Gerrit http login");
                          String password = userInput.askNonBlank("Gerrit http password");
                          return conf.setAndGetSite(rootUrl, user, password);
                        }));
  }
}
