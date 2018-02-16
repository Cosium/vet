package com.cosium.vet.git;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GitRepository {

  /** @return The remote tracked by current branch */
  String getBranchRemote();

  /** @return The remote ref tracked by the current branch */
  String getBranchMerge();

  /**
   * @param otherBranch The other branch
   * @return The most recent common ancestor between current branch and <code>otherBranch</code>
   */
  String getMostRecentCommonCommit(String otherBranch);

  /**
   * @return Get current tree
   */
  String getTree();

  /**
   * @param tree An existing tree object
   * @param parent The id of the parent commit object
   * @param commitMessage The message of the created commit
   * @return The id of the created commit
   */
  String commitTree(String tree, String parent, String commitMessage);
}
