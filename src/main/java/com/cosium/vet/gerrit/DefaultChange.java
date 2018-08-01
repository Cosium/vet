package com.cosium.vet.gerrit;

import com.cosium.vet.git.RevisionId;

import static java.util.Objects.requireNonNull;

/**
 * Created on 01/08/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultChange implements Change {

  private final PatchsetRepository patchsetRepository;
  private final PushUrl pushUrl;
  private final ChangeNumericId numericId;

  private DefaultChange(
      PatchsetRepository patchsetRepository, PushUrl pushUrl, ChangeNumericId numericId) {
    this.patchsetRepository = requireNonNull(patchsetRepository);
    this.pushUrl = requireNonNull(pushUrl);
    this.numericId = requireNonNull(numericId);
  }

  @Override
  public ChangeNumericId getNumericId() {
    return numericId;
  }

  @Override
  public RevisionId fetchRevision() {
    return patchsetRepository
        .findLatestPatchset(numericId)
        .map(Patchset::getRevision)
        .orElseThrow(
            () -> new RuntimeException("Could not find latest patchset of change " + numericId));
  }

  @Override
  public RevisionId fetchParent() {
    return patchsetRepository.findPatchset(numericId, 1).getParent();
  }

  @Override
  public String getWebUrl() {
    return pushUrl.computeChangeWebUrl(numericId);
  }

  @Override
  public String toString() {
    return getWebUrl() + " ";
  }

  public static class Factory implements ChangeFactory {

    private final PatchsetRepository patchsetRepository;
    private final PushUrl pushUrl;

    Factory(PatchsetRepository patchsetRepository, PushUrl pushUrl) {
      this.patchsetRepository = requireNonNull(patchsetRepository);
      this.pushUrl = requireNonNull(pushUrl);
    }

    @Override
    public Change build(ChangeNumericId changeNumericId) {
      return new DefaultChange(patchsetRepository, pushUrl, changeNumericId);
    }
  }
}
