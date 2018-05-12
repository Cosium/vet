package com.cosium.vet.gerrit;

import com.cosium.vet.gerrit.config.GerritConfiguration;
import com.cosium.vet.gerrit.config.GerritConfigurationRepository;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.RemoteName;
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
  private final GitClient git;

  DefaultChangeRepository(
      GerritConfigurationRepository configurationRepository,
      ChangeFactory changeFactory,
      PatchSetRepository patchSetRepository,
      GitClient git) {
    this.configurationRepository = requireNonNull(configurationRepository);
    this.changeFactory = requireNonNull(changeFactory);
    this.patchSetRepository = requireNonNull(patchSetRepository);
    this.git = requireNonNull(git);
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
  public Change checkoutAndTrackChange(
      ChangeCheckoutBranchName checkoutBranch,
      ChangeNumericId numericId,
      BranchShortName branchShortName) {
    Patch latestPatch =
        patchSetRepository
            .findLastestPatch(numericId)
            .orElseThrow(
                () -> new RuntimeException("No patch found for change with id " + numericId));
    git.fetch(RemoteName.ORIGIN, numericId.branchRefName(latestPatch));
    git.checkoutFetchHead();
    git.checkoutNewBranch(checkoutBranch.toBranchShortName());
    return trackChange(numericId, branchShortName);
  }

  @Override
  public Change createAndTrackChange(BranchShortName targetBranch) {
    Patch patch = patchSetRepository.createPatch(targetBranch, null, null);
    return trackChange(patch.getChangeNumericId(), targetBranch);
  }

  @Override
  public Change createChange(BranchShortName targetBranch) {
    Patch patch = patchSetRepository.createPatch(targetBranch, null, null);
    return changeFactory.build(targetBranch, patch.getChangeNumericId());
  }

  @Override
  public boolean exists(ChangeNumericId numericId) {
    return patchSetRepository.findLastestPatch(numericId).isPresent();
  }

  @Override
  public String pull() {
    Change change =
        getTrackedChange()
            .orElseThrow(() -> new RuntimeException("There is no currently tracked change"));
    return patchSetRepository.pullLatest(change.getNumericId());
  }
}
