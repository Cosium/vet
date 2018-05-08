package com.cosium.vet.gerrit;

import com.cosium.vet.gerrit.config.DefaultGerritConfigurationRepositoryFactory;
import com.cosium.vet.gerrit.config.GerritConfigurationRepository;
import com.cosium.vet.gerrit.config.GerritConfigurationRepositoryFactory;
import com.cosium.vet.git.*;
import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;

import java.net.URL;

import static java.util.Objects.requireNonNull;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultGerritChangeRepositoryFactory implements GerritChangeRepositoryFactory {

  private static final Logger LOG =
      LoggerFactory.getLogger(DefaultGerritChangeRepositoryFactory.class);

  private final GerritConfigurationRepositoryFactory configurationRepositoryFactory;
  private final GitClientFactory gitClientFactory;

  public DefaultGerritChangeRepositoryFactory(
      GitConfigRepositoryFactory gitConfigRepositoryfactory, GitClientFactory gitClientFactory) {
    this(
        new DefaultGerritConfigurationRepositoryFactory(gitConfigRepositoryfactory),
        gitClientFactory);
  }

  public DefaultGerritChangeRepositoryFactory(
      GerritConfigurationRepositoryFactory configurationRepositoryFactory,
      GitClientFactory gitClientFactory) {
    this.configurationRepositoryFactory = requireNonNull(configurationRepositoryFactory);
    this.gitClientFactory = requireNonNull(gitClientFactory);
  }

  @Override
  public GerritChangeRepository build() {
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
                        String.format(
                            "Could not find url of remote '%s'. Please verify that you are in a valid git repository.",
                            remote)));

    GerritPushUrl pushUrl = GerritPushUrl.of(remoteUrl.toString());
    LOG.debug("Gerrit push url is {}", pushUrl);
    GerritProjectName projectName = pushUrl.parseProjectName();
    LOG.debug("Gerrit project is '{}'", projectName);

    GerritPatchSetRepository patchSetRepository =
        new DefaultGerritPatchSetRepository(gitClient, pushUrl);
    PatchSetCommitMessageFactory patchSetCommitMessageFactory =
        new DefaultPatchSetCommitMessageFactory(gitClient, patchSetRepository);
    GerritChangeFactory changeFactory =
        new DefaultGerritChange.Factory(gitClient, patchSetCommitMessageFactory, pushUrl);

    return new DefaultGerritChangeRepository(
        configurationRepository, changeFactory, patchSetRepository);
  }
}
