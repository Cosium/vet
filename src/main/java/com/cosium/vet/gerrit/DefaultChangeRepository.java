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
  private final AlterableChangeFactory alterableChangeFactory;
  private final PatchsetRepository patchsetRepository;
  private final GitClient git;

  DefaultChangeRepository(
      GerritConfigurationRepository configurationRepository,
      ChangeFactory changeFactory,
      AlterableChangeFactory alterableChangeFactory,
      PatchsetRepository patchsetRepository,
      GitClient git) {
    this.configurationRepository = requireNonNull(configurationRepository);
    this.changeFactory = requireNonNull(changeFactory);
    this.alterableChangeFactory = requireNonNull(alterableChangeFactory);
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
  public Optional<AlterableChange> getTrackedChange() {
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

    return Optional.of(alterableChangeFactory.build(changeTargetBranch, changeNumericId));
  }

  @Override
  public AlterableChange trackChange(ChangeNumericId numericId, BranchShortName targetBranch) {
    LOG.debug("Enabling change for numeric id {}", numericId);
    return configurationRepository.readAndWrite(
        conf -> {
          conf.setTrackedChangeNumericId(numericId);
          conf.setTrackedChangeTargetBranch(targetBranch);
          return alterableChangeFactory.build(targetBranch, numericId);
        });
  }

  @Override
  public AlterableChange checkoutAndTrackChange(
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
  public CreatedChange createAndTrackChange(BranchShortName targetBranch, PatchsetOptions firstPatchsetOptions) {
    CreatedPatchset patch =
        patchsetRepository.createChangeFirstPatchset(targetBranch, firstPatchsetOptions);
    AlterableChange change = trackChange(patch.getChangeNumericId(), targetBranch);
    return new DefaultCreatedChange(change, patch.getCreationLog());
  }

  @Override
  public CreatedChange createChange(BranchShortName targetBranch, PatchsetOptions firstPatchsetOptions) {
    CreatedPatchset patch =
        patchsetRepository.createChangeFirstPatchset(targetBranch, firstPatchsetOptions);
    AlterableChange change = alterableChangeFactory.build(targetBranch, patch.getChangeNumericId());
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

  @Override
  public Optional<Change> findChange(ChangeNumericId numericId) {
    if (!exists(numericId)) {
      return Optional.empty();
    }
    return Optional.of(changeFactory.build(numericId));
  }

  private class DefaultCreatedChange implements CreatedChange {

    private final AlterableChange change;
    private final String creationLog;

    private DefaultCreatedChange(AlterableChange change, String creationLog) {
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
    public RevisionId fetchRevision() {
      return change.fetchRevision();
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
