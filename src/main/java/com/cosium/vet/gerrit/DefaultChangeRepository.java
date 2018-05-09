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
class DefaultChangeRepository implements ChangeRepository {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultChangeRepository.class);

  private final GerritConfigurationRepository configurationRepository;
  private final ChangeFactory changeFactory;
  private final PatchSetRepository patchSetRepository;

  DefaultChangeRepository(
      GerritConfigurationRepository configurationRepository,
      ChangeFactory changeFactory,
      PatchSetRepository patchSetRepository) {
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
  public Optional<Change> getTrackedChange() {
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

    return Optional.of(changeFactory.build(changeTargetBranch, changeNumericId));
  }

  @Override
  public Change trackChange(ChangeNumericId numericId, BranchShortName targetBranch) {
    LOG.debug("Enabling change for numeric id {}", numericId);
    return configurationRepository.readAndWrite(
        conf -> {
          conf.setTrackedChangeNumericId(numericId);
          conf.setTrackedChangeTargetBranch(targetBranch);
          return changeFactory.build(targetBranch, numericId);
        });
  }

  @Override
  public Change trackNewChange(BranchShortName targetBranch) {
    Patch patch = patchSetRepository.createPatch(targetBranch, null, null);
    return trackChange(patch.getChangeNumericId(), targetBranch);
  }

  @Override
  public boolean exists(ChangeNumericId numericId) {
    return patchSetRepository.getLastestPatchSetCommitMessage(numericId).isPresent();
  }
}
