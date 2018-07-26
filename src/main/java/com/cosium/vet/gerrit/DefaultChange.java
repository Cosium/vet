package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.RevisionId;

import static java.util.Objects.requireNonNull;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class DefaultChange implements Change {

  private final PatchsetRepository patchsetRepository;
  private final PushUrl pushUrl;
  private final BranchShortName targetBranch;
  private final ChangeNumericId numericId;

  private DefaultChange(
      PatchsetRepository patchsetRepository,
      PushUrl pushUrl,
      BranchShortName targetBranch,
      ChangeNumericId numericId) {
    this.patchsetRepository = requireNonNull(patchsetRepository);
    this.pushUrl = requireNonNull(pushUrl);
    this.targetBranch = requireNonNull(targetBranch);
    this.numericId = requireNonNull(numericId);
  }

  @Override
  public ChangeNumericId getNumericId() {
    return numericId;
  }

  @Override
  public RevisionId fetchParent() {
    return patchsetRepository.findPatchset(numericId, 1).getParent();
  }

  @Override
  public String createPatchset(PatchsetOptions options) {
    return patchsetRepository.createPatchset(targetBranch, numericId, options).getCreationLog();
  }

  @Override
  public String getWebUrl() {
    return pushUrl.computeChangeWebUrl(numericId);
  }

  @Override
  public String toString() {
    return getWebUrl() + " ";
  }

  /**
   * Created on 08/05/18.
   *
   * @author Reda.Housni-Alaoui
   */
  static class Factory implements ChangeFactory {

    private final PatchsetRepository patchsetRepository;
    private final PushUrl pushUrl;

    Factory(PatchsetRepository patchsetRepository, PushUrl pushUrl) {
      this.patchsetRepository = requireNonNull(patchsetRepository);
      this.pushUrl = requireNonNull(pushUrl);
    }

    @Override
    public Change build(BranchShortName targetBranch, ChangeNumericId changeNumericId) {
      return new DefaultChange(patchsetRepository, pushUrl, targetBranch, changeNumericId);
    }
  }
}
