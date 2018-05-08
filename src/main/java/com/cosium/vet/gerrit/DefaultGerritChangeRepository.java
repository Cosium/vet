package com.cosium.vet.gerrit;

import com.cosium.vet.gerrit.config.GerritConfiguration;
import com.cosium.vet.gerrit.config.GerritConfigurationRepository;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class DefaultGerritChangeRepository implements GerritChangeRepository {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultGerritChangeRepository.class);

  private final GerritConfigurationRepository configurationRepository;
  private final GerritChangeFactory changeFactory;
  private final GerritPatchSetRepository patchSetRepository;

  DefaultGerritChangeRepository(
      GerritConfigurationRepository configurationRepository,
      GerritChangeFactory changeFactory,
      GerritPatchSetRepository patchSetRepository) {
    this.configurationRepository = requireNonNull(configurationRepository);
    this.changeFactory = requireNonNull(changeFactory);
    this.patchSetRepository = requireNonNull(patchSetRepository);
  }

  @Override
  public void untrack() {
    configurationRepository.readAndWrite(
        gerritConfiguration -> {
          gerritConfiguration.setTrackedChangeTargetBranch(null);
          gerritConfiguration.setTrackedChangeNumericId(null);
          return null;
        });
  }

  /** @return The current change */
  @Override
  public Optional<GerritChange> getTrackedChange() {
    GerritConfiguration gerritConfiguration = configurationRepository.read();

    ChangeNumericId changeNumericId = gerritConfiguration.getTrackedChangeNumericId().orElse(null);
    if (changeNumericId == null) {
      return Optional.empty();
    }

    BranchShortName changeTargetBranch =
        gerritConfiguration.getTrackedChangeTargetBranch().orElse(null);
    if (changeTargetBranch == null) {
      return Optional.empty();
    }

    return Optional.of(changeFactory.build(changeNumericId, changeTargetBranch));
  }

  @Override
  public GerritChange trackChange(ChangeNumericId numericId, BranchShortName targetBranch) {
    LOG.debug("Enabling change for numeric id {}", numericId);
    return configurationRepository.readAndWrite(
        conf -> {
          conf.setTrackedChangeNumericId(numericId);
          conf.setTrackedChangeTargetBranch(targetBranch);
          return changeFactory.build(numericId, targetBranch);
        });
  }

  @Override
  public boolean exists(ChangeNumericId numericId) {
    return patchSetRepository.getLastestPatchSetCommitMessage(numericId).isPresent();
  }
}
