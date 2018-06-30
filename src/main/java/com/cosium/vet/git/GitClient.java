package com.cosium.vet.git;

import java.util.List;
import java.util.Optional;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GitClient {

  /** @return The current branch */
  BranchShortName getBranch();

  /**
   * @param branch The branch to look for
   * @return The remote tracked by the branch
   */
  Optional<RemoteName> getRemote(BranchShortName branch);

  /**
   * @param remoteName The remote name
   * @return The url of the remote
   */
  Optional<RemoteUrl> getRemotePushUrl(RemoteName remoteName);

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
  String commitTree(String tree, String parent, CommitMessage commitMessage);

  /** @return The last commit message */
  CommitMessage getLastCommitMessage();

  /**
   * @param revisionId The wanted revision id
   * @return The commit message of the provided revision id
   */
  CommitMessage getCommitMessage(RevisionId revisionId);

  /**
   * @param revisionId The child revision id
   * @return The parent of the provided revision id
   */
  RevisionId getParent(RevisionId revisionId);

  /**
   * Push the refspec to remote
   *
   * @param remote The remote to push to
   * @param refspec The refspec to push
   * @return The command output
   */
  String push(String remote, String refspec);

  /**
   * @param remote The remote to look for
   * @return All refs for the provided remote
   */
  List<BranchRef> listRemoteRefs(RemoteName remote);

  /**
   * Fetches the ref from remote
   *
   * @param remote The remote to fetch from
   * @param branchRefName The ref name to fetch
   */
  void fetch(RemoteName remote, BranchRefName branchRefName);

  /**
   * Fetches the branch form remote
   *
   * @param remote The remote to fetch from
   * @param branchShortName The branch to fetch
   */
  void fetch(RemoteName remote, BranchShortName branchShortName);

  /**
   * Pull the ref from remote
   *
   * @param remote The remote to fetch from
   * @param branchRefName The ref name to fetch
   * @return The command output
   */
  String pull(RemoteName remote, BranchRefName branchRefName);

  /** @return The printed git status */
  String status();

  String checkoutFetchHead();

  String checkoutNewBranch(BranchShortName branchShortName);

  /**
   * Reset the current branch to the provided revision id. Preserves local changes.
   *
   * @param revisionId The revision of to reset to
   * @return The command output
   */
  String resetKeep(RevisionId revisionId);
}
