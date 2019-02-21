package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.CommitMessage;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.RemoteName;
import com.cosium.vet.git.RevisionId;

import java.util.List;

import static java.util.Objects.requireNonNull;

/** @author Réda Housni Alaoui */
public class TargetBranch {

  private final GitClient git;
  private final BranchShortName shortName;

  private TargetBranch(GitClient git, BranchShortName shortName) {
    this.git = requireNonNull(git);
    this.shortName = requireNonNull(shortName);
  }

  public RevisionId computeChangeStartRevision() {
    RemoteName remote =
        git.getRemote(shortName)
            .orElseThrow(
                () ->
                    new RuntimeException(
                        String.format("No remote found for branch '%s'", shortName)));

    RevisionId oldestRevision =
        RevisionId.of(git.getMostRecentCommonCommit(String.format("%s/%s", remote, shortName)));

    RevisionId walkedRevision = git.getHeadRevisionId();
    while (!oldestRevision.equals(walkedRevision)) {
      CommitMessage commitMessage = git.getCommitMessage(walkedRevision);

      List<RevisionId> parents = git.getParents(walkedRevision);
      if (parents.isEmpty()) {
        break;
      }
      if (parents.size() > 1) {
        throw new RuntimeException(
            walkedRevision
                + " is a merge commit. Vet does not support merge commits. Always use rebase instead of merge.");
      }
      walkedRevision = parents.get(0);
    }

    return oldestRevision;
  }

  public static class Factory {

    private final GitClient git;

    public Factory(GitClient git) {
      this.git = requireNonNull(git);
    }

    public TargetBranch build(BranchShortName shortName) {
      return new TargetBranch(git, shortName);
    }
  }
}
