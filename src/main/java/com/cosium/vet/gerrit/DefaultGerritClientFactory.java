package com.cosium.vet.gerrit;

import com.cosium.vet.file.FileSystem;
import com.cosium.vet.gerrit.config.DefaultGerritConfigurationRepositoryFactory;
import com.cosium.vet.gerrit.config.GerritConfigurationRepository;
import com.cosium.vet.gerrit.config.GerritConfigurationRepositoryFactory;
import com.cosium.vet.git.*;
import com.cosium.vet.runtime.UserInput;
import com.google.gerrit.extensions.api.GerritApi;
import com.urswolfer.gerrit.client.rest.GerritRestApiFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

import static java.util.Objects.requireNonNull;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultGerritClientFactory implements GerritClientFactory {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultGerritClientFactory.class);

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
  public GerritClient build(GerritUser user, GerritPassword password) {
    GitClient gitClient = gitClientFactory.build();
    GerritConfigurationRepository configurationRepository = configurationRepositoryFactory.build();

    RemoteName remote = RemoteName.ORIGIN;
    URL remoteUrl =
        gitClient
            .getRemoteUrl(remote)
            .map(RemoteUrl::toURL)
            .orElseThrow(
                () ->
                    new RuntimeException(
                        String.format("Could not find url of remote '%s'", remote)));

    GerritPushUrl pushUrl = GerritPushUrl.of(remoteUrl.toString());
    LOG.debug("Gerrit push url is {}", pushUrl);
    GerritHttpRootUrl rootUrl = pushUrl.parseHttpRootUrl();
    LOG.debug("Gerrit root url is {}", rootUrl);
    GerritProjectName project = pushUrl.parseProjectName();
    LOG.debug("Gerrit project is '{}'", project);

    GerritApiBuilder gerritApiBuilder =
        new GerritApiBuilder(
            configurationRepository, gerritRestApiFactory, userInput, rootUrl, user, password);
    GerritApi gerritApi = gerritApiBuilder.build();
    return new DefaultGerritClient(gitClient, configurationRepository, gerritApi, pushUrl, project);
  }
}
