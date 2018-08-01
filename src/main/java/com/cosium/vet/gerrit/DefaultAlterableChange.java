package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.RevisionId;

import static java.util.Objects.requireNonNull;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class DefaultAlterableChange implements AlterableChange {

  private final Change delegate;
  private final PatchsetRepository patchsetRepository;
  private final BranchShortName targetBranch;
  private final ChangeNumericId numericId;

  private DefaultAlterableChange(
      Change delegate,
      PatchsetRepository patchsetRepository,
      BranchShortName targetBranch,
      ChangeNumericId numericId) {
    this.delegate = requireNonNull(delegate);
    this.patchsetRepository = requireNonNull(patchsetRepository);
    this.targetBranch = requireNonNull(targetBranch);
    this.numericId = requireNonNull(numericId);
  }

  @Override
  public ChangeNumericId getNumericId() {
    return delegate.getNumericId();
  }

  @Override
  public RevisionId fetchRevision() {
    return delegate.fetchRevision();
  }

  @Override
  public RevisionId fetchParent() {
    return delegate.fetchParent();
  }

  @Override
  public String createPatchset(PatchsetOptions options) {
    return patchsetRepository.createPatchset(targetBranch, numericId, options).getCreationLog();
  }

  @Override
  public String getWebUrl() {
    return delegate.getWebUrl();
  }

  @Override
  public String toString() {
    return delegate.toString();
  }

  /**
   * Created on 08/05/18.
   *
   * @author Reda.Housni-Alaoui
   */
  static class Factory implements AlterableChangeFactory {

    private final ChangeFactory changeFactory;
    private final PatchsetRepository patchsetRepository;

    Factory(ChangeFactory changeFactory, PatchsetRepository patchsetRepository) {
      this.changeFactory = requireNonNull(changeFactory);
      this.patchsetRepository = requireNonNull(patchsetRepository);
    }

    @Override
    public AlterableChange build(BranchShortName targetBranch, ChangeNumericId changeNumericId) {
      Change delegate = changeFactory.build(changeNumericId);
      return new DefaultAlterableChange(
          delegate, patchsetRepository, targetBranch, changeNumericId);
    }
  }
}
