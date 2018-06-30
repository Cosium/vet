package com.cosium.vet.git;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Created on 29/06/18.
 *
 * @author Reda.Housni-Alaoui
 */
class WindowsGitClient implements GitClient {

  private final GitClient delegate;

  public WindowsGitClient(GitClient delegate) {
    this.delegate = requireNonNull(delegate);
  }

  @Override
  public BranchShortName getBranch() {
    return delegate.getBranch();
  }

  @Override
  public Optional<RemoteName> getRemote(BranchShortName branch) {
    return delegate.getRemote(branch);
  }

  @Override
  public Optional<RemoteUrl> getRemotePushUrl(RemoteName remoteName) {
    return delegate.getRemotePushUrl(remoteName);
  }

  @Override
  public String getMostRecentCommonCommit(String otherBranch) {
    return delegate.getMostRecentCommonCommit(otherBranch);
  }

  @Override
  public String getTree() {
    return delegate.getTree();
  }

  @Override
  public String commitTree(String tree, String parent, CommitMessage commitMessage) {
    return delegate.commitTree(tree, parent, commitMessage.escapeQuotes());
  }

  @Override
  public CommitMessage getLastCommitMessage() {
    return delegate.getLastCommitMessage();
  }

  @Override
  public CommitMessage getCommitMessage(RevisionId revisionId) {
    return delegate.getCommitMessage(revisionId);
  }

  @Override
  public RevisionId getParent(RevisionId revisionId) {
    return delegate.getParent(revisionId);
  }

  @Override
  public String push(String remote, String refspec) {
    return delegate.push(remote, refspec);
  }

  @Override
  public List<BranchRef> listRemoteRefs(RemoteName remote) {
    return delegate.listRemoteRefs(remote);
  }

  @Override
  public void fetch(RemoteName remote, BranchRefName branchRefName) {
    delegate.fetch(remote, branchRefName);
  }

  @Override
  public void fetch(RemoteName remote, BranchShortName branchShortName) {
    delegate.fetch(remote, branchShortName);
  }

  @Override
  public String pull(RemoteName remote, BranchRefName branchRefName) {
    return delegate.pull(remote, branchRefName);
  }

  @Override
  public String status() {
    return delegate.status();
  }

  @Override
  public String checkoutFetchHead() {
    return delegate.checkoutFetchHead();
  }

  @Override
  public String checkoutNewBranch(BranchShortName branchShortName) {
    return delegate.checkoutNewBranch(branchShortName);
  }

  @Override
  public String resetKeep(RevisionId revisionId) {
    return delegate.resetKeep(revisionId);
  }
}
