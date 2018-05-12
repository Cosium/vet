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
  public CreatedChange createAndTrackChange(
      BranchShortName targetBranch, PatchOptions firstPatchOptions) {
    CreatedPatch patch = patchSetRepository.createPatch(targetBranch, firstPatchOptions);
    Change change = trackChange(patch.getChangeNumericId(), targetBranch);
    return new DefaultCreatedChange(change, patch.getCreationLog());
  }

  @Override
  public CreatedChange createChange(BranchShortName targetBranch, PatchOptions firstPatchOptions) {
    CreatedPatch patch = patchSetRepository.createPatch(targetBranch, firstPatchOptions);
    Change change = changeFactory.build(targetBranch, patch.getChangeNumericId());
    return new DefaultCreatedChange(change, patch.getCreationLog());
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
    public String createPatch(PatchOptions options) {
      return change.createPatch(options);
    }

    @Override
    public String toString() {
      return change.toString();
    }
  }
}
