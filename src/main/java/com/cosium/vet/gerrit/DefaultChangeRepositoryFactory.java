package com.cosium.vet.gerrit;

import com.cosium.vet.gerrit.config.DefaultGerritConfigurationRepositoryFactory;
import com.cosium.vet.gerrit.config.GerritConfigurationRepository;
import com.cosium.vet.gerrit.config.GerritConfigurationRepositoryFactory;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.GitConfigRepositoryFactory;
import com.cosium.vet.git.RemoteName;
import com.cosium.vet.git.RemoteUrl;
import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;

import java.net.URL;

import static java.util.Objects.requireNonNull;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultChangeRepositoryFactory implements ChangeRepositoryFactory {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultChangeRepositoryFactory.class);

  private final GerritConfigurationRepositoryFactory configurationRepositoryFactory;
  private final GitClient git;

  public DefaultChangeRepositoryFactory(
      GitConfigRepositoryFactory gitConfigRepositoryfactory, GitClient git) {
    this(new DefaultGerritConfigurationRepositoryFactory(gitConfigRepositoryfactory), git);
  }

  public DefaultChangeRepositoryFactory(
      GerritConfigurationRepositoryFactory configurationRepositoryFactory, GitClient git) {
    this.configurationRepositoryFactory = requireNonNull(configurationRepositoryFactory);
    this.git = requireNonNull(git);
  }

  @Override
  public ChangeRepository build() {
    GerritConfigurationRepository configurationRepository = configurationRepositoryFactory.build();

    RemoteName remote = RemoteName.ORIGIN;
    URL remoteUrl =
        git.getRemotePushUrl(remote)
            .map(RemoteUrl::toURL)
            .orElseThrow(
                () ->
                    new RuntimeException(
                        String.format(
                            "Could not find url of remote '%s'. Please verify that you are in a valid git repository.",
                            remote)));

    PushUrl pushUrl = PushUrl.of(remoteUrl.toString());
    LOG.debug("Gerrit push url is {}", pushUrl);
    ProjectName projectName = pushUrl.parseProjectName();
    LOG.debug("Gerrit project is '{}'", projectName);

    PatchsetCommitMessageFactory commitMessageFactory =
        new DefaultPatchsetCommitMessageFactory(git);
    PatchsetRepository patchSetRepository =
        new DefaultPatchsetRepository(git, pushUrl, commitMessageFactory);

    ChangeFactory changeFactory = new DefaultChange.Factory(patchSetRepository, pushUrl);
    AlterableChangeFactory alterableChangeFactory =
        new DefaultAlterableChange.Factory(changeFactory, patchSetRepository);

    return new DefaultChangeRepository(
        configurationRepository, changeFactory, alterableChangeFactory, patchSetRepository, git);
  }
}
