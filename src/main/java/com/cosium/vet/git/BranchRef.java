package com.cosium.vet.git;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Created on 27/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class BranchRef {

  private final RevisionId revisionId;
  private final BranchRefName branchRefName;

  public BranchRef(RevisionId revisionId, BranchRefName branchRefName) {
    requireNonNull(revisionId);
    requireNonNull(branchRefName);
    this.revisionId = revisionId;
    this.branchRefName = branchRefName;
  }

  public RevisionId getRevisionId() {
    return revisionId;
  }

  public BranchRefName getBranchRefName() {
    return branchRefName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BranchRef branchRef = (BranchRef) o;
    return Objects.equals(branchRefName, branchRef.branchRefName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(branchRefName);
  }
}
