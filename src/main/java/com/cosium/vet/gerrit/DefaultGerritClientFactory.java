package com.cosium.vet.gerrit;

import com.cosium.vet.gerrit.config.DefaultGerritConfigurationRepositoryFactory;
import com.cosium.vet.gerrit.config.GerritConfigurationRepository;
import com.cosium.vet.gerrit.config.GerritConfigurationRepositoryFactory;
import com.cosium.vet.git.*;
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

  public DefaultGerritClientFactory(
      GitConfigRepositoryFactory gitConfigRepositoryfactory, GitClientFactory gitClientFactory) {
    this(
        new DefaultGerritConfigurationRepositoryFactory(gitConfigRepositoryfactory),
        gitClientFactory);
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
    GerritProjectName project = pushUrl.parseProjectName();
    LOG.debug("Gerrit project is '{}'", project);

    return new DefaultGerritClient(configurationRepository, gitClient, pushUrl, project);
  }
}
