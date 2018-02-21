package com.cosium.vet.git;

import java.util.Optional;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GitClient {

  /**
   * @param branch The branch to look for
   * @return The remote tracked by the branch
   */
  Optional<RemoteName> getRemote(BranchShortName branch);

  /** @return The remote tracked by current branch */
  Optional<RemoteName> getBranchRemote();

  /**
   * @param remoteName The remote name
   * @return The url of the remote
   */
  Optional<RemoteUrl> getRemoteUrl(RemoteName remoteName);

  /** @return The remote ref tracked by the current branch */
  String getBranchMerge();

  /**
   * @param otherBranch The other branch
   * @return The most recent common ancestor between current branch and <code>otherBranch</code>
   */
  String getMostRecentCommonCommit(String otherBranch);

  /** @return Get current tree */
  String getTree();

  /**
   * @param tree An existing tree object
   * @param parent The id of the parent commit object
   * @param commitMessage The message of the created commit
   * @return The id of the created commit
   */
  String commitTree(String tree, String parent, String commitMessage);

  String writeTree();

  String revParse(String revision);

  String var(String varName);

  String hashObject(String type, String content);

  String getLastCommitMessage();

  void push(String remote, String refspec);
}
