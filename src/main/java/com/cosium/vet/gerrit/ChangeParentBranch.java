package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.RemoteName;
import com.cosium.vet.git.RevisionId;

import static java.util.Objects.requireNonNull;

/**
 * Created on 01/08/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class ChangeParentBranch implements ChangeParent {

  private final GitClient git;
  private final BranchShortName branch;

  private ChangeParentBranch(GitClient git, BranchShortName branch) {
    this.git = requireNonNull(git);
    this.branch = requireNonNull(branch);
  }

  @Override
  public RevisionId getRevision() {
    RemoteName remote =
        git.getRemote(branch)
            .orElseThrow(
                () ->
                    new RuntimeException(String.format("No remote found for branch '%s'", branch)));
    return RevisionId.of(git.getMostRecentCommonCommit(String.format("%s/%s", remote, branch)));
  }

  public static class Factory implements ChangeParentBranchFactory {
    private final GitClient git;

    public Factory(GitClient git) {
      this.git = requireNonNull(git);
    }

    @Override
    public ChangeParent build(BranchShortName branch) {
      return new ChangeParentBranch(git, branch);
    }
  }
}
