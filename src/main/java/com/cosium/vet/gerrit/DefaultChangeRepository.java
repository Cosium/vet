package com.cosium.vet.gerrit;

import com.cosium.vet.gerrit.config.GerritConfiguration;
import com.cosium.vet.gerrit.config.GerritConfigurationRepository;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.RemoteName;
import com.cosium.vet.git.RevisionId;
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
  private final PatchsetRepository patchsetRepository;
  private final GitClient git;

  DefaultChangeRepository(
      GerritConfigurationRepository configurationRepository,
      ChangeFactory changeFactory,
      PatchsetRepository patchsetRepository,
      GitClient git) {
    this.configurationRepository = requireNonNull(configurationRepository);
    this.changeFactory = requireNonNull(changeFactory);
    this.patchsetRepository = requireNonNull(patchsetRepository);
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
      BranchShortName targetBranch) {
    Patchset latestPatchset =
        patchsetRepository
            .findLatestPatchset(numericId)
            .orElseThrow(
                () -> new RuntimeException("No patchset found for change with id " + numericId));
    git.fetch(RemoteName.ORIGIN, numericId.branchRefName(latestPatchset));
    git.checkoutFetchHead();
    git.checkoutNewBranch(checkoutBranch.toBranchShortName());
    return trackChange(numericId, targetBranch);
  }

  @Override
  public CreatedChange createAndTrackChange(
          ChangeParent parent, BranchShortName targetBranch, PatchsetOptions firstPatchsetOptions) {
    CreatedPatchset patch =
        patchsetRepository.createChangeFirstPatchset(parent, targetBranch, firstPatchsetOptions);
    Change change = trackChange(patch.getChangeNumericId(), targetBranch);
    return new DefaultCreatedChange(change, patch.getCreationLog());
  }

  @Override
  public CreatedChange createChange(
          ChangeParent parent, BranchShortName targetBranch, PatchsetOptions firstPatchsetOptions) {
    CreatedPatchset patch =
        patchsetRepository.createChangeFirstPatchset(parent, targetBranch, firstPatchsetOptions);
    Change change = changeFactory.build(targetBranch, patch.getChangeNumericId());
    return new DefaultCreatedChange(change, patch.getCreationLog());
  }

  @Override
  public boolean exists(ChangeNumericId numericId) {
    return patchsetRepository.findLatestPatchset(numericId).isPresent();
  }

  @Override
  public String pull() {
    Change change =
        getTrackedChange()
            .orElseThrow(() -> new RuntimeException("There is no currently tracked change"));
    return patchsetRepository.pullLatestPatchset(change.getNumericId());
  }

  private class DefaultCreatedChange implements CreatedChange {

    private final Change change;
    private final String creationLog;

    private DefaultCreatedChange(Change change, String creationLog) {
      this.change = requireNonNull(change);
      this.creationLog = requireNonNull(creationLog);
    }

    @Override
    public String getCreationLog() {
      return creationLog;
    }

    @Override
    public ChangeNumericId getNumericId() {
      return change.getNumericId();
    }

    @Override
    public RevisionId fetchParent() {
      return change.fetchParent();
    }

    @Override
    public String createPatchset(PatchsetOptions options) {
      return change.createPatchset(options);
    }

    @Override
    public String getWebUrl() {
      return change.getWebUrl();
    }

    @Override
    public String toString() {
      return change.toString();
    }
  }
}
