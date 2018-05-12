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

  private final PatchSetRepository patchSetRepository;
  private final PushUrl pushUrl;
  private final BranchShortName targetBranch;
  private final ChangeNumericId numericId;

  private DefaultChange(
      PatchSetRepository patchSetRepository,
      PushUrl pushUrl,
      BranchShortName targetBranch,
      ChangeNumericId numericId) {
    this.patchSetRepository = requireNonNull(patchSetRepository);
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
    return patchSetRepository.findPatch(numericId, 1).getParent();
  }

  @Override
  public String createPatch(PatchOptions options) {
    return patchSetRepository.createPatch(targetBranch, numericId, options).getCreationLog();
  }

  @Override
  public String toString() {
    return pushUrl.computeChangeWebUrl(numericId) + " ";
  }

  /**
   * Created on 08/05/18.
   *
   * @author Reda.Housni-Alaoui
   */
  static class Factory implements ChangeFactory {

    private final PatchSetRepository patchSetRepository;
    private final PushUrl pushUrl;

    Factory(PatchSetRepository patchSetRepository, PushUrl pushUrl) {
      this.patchSetRepository = requireNonNull(patchSetRepository);
      this.pushUrl = requireNonNull(pushUrl);
    }

    @Override
    public Change build(BranchShortName targetBranch, ChangeNumericId changeNumericId) {
      return new DefaultChange(patchSetRepository, pushUrl, targetBranch, changeNumericId);
    }
  }
}
