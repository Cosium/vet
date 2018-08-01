package com.cosium.vet.gerrit;

import com.cosium.vet.git.RevisionId;

import static java.util.Objects.requireNonNull;

/**
 * Used when the parent of a change is another change
 *
 * <p>Created on 01/08/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class ChangeParentChange implements ChangeParent {

  private final ChangeRepository changeRepository;
  private final ChangeNumericId parentChangeNumericId;

  private ChangeParentChange(
      ChangeRepository changeRepository, ChangeNumericId parentChangeNumericId) {
    this.changeRepository = requireNonNull(changeRepository);
    this.parentChangeNumericId = requireNonNull(parentChangeNumericId);
  }

  @Override
  public RevisionId getRevision() {
    return changeRepository
        .findChange(parentChangeNumericId)
        .map(Change::fetchRevision)
        .orElseThrow(
            () -> new RuntimeException("No change found for numeric id " + parentChangeNumericId));
  }

  public static class Factory implements ChangeParentChangeFactory {

    private final ChangeRepositoryFactory changeRepositoryFactory;

    public Factory(ChangeRepositoryFactory changeRepositoryFactory) {
      this.changeRepositoryFactory = requireNonNull(changeRepositoryFactory);
    }

    @Override
    public ChangeParent build(ChangeNumericId parentChangeNumericId) {
      return new ChangeParentChange(changeRepositoryFactory.build(), parentChangeNumericId);
    }
  }
}
