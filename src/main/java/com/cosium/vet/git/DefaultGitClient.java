package com.cosium.vet.git;

import static java.util.Objects.requireNonNull;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class DefaultGitClient implements GitClient {

  private final GitExecutor git;

  DefaultGitClient(GitExecutor gitExecutor) {
    requireNonNull(gitExecutor);
    this.git = gitExecutor;
  }

  @Override
  public String getBranchRemote() {
    String branchShortName = getBranchShortName();
    return git.execute("config", String.format("branch.%s.remote", branchShortName));
  }

  @Override
  public String getBranchMerge() {
    String branchShortName = getBranchShortName();
    return git.execute("config", String.format("branch.%s.merge", branchShortName));
  }

  @Override
  public String getMostRecentCommonCommit(String otherBranch) {
    return git.execute("merge-base", "HEAD", otherBranch);
  }

  @Override
  public String getTree() {
    return git.execute("rev-parse", "HEAD:");
  }

  @Override
  public String commitTree(String tree, String parent, String commitMessage) {
    return git.execute("commit-tree", tree, "-p", parent, "-m", commitMessage);
  }

  private String getBranchShortName() {
    return git.execute("rev-parse", "--abbrev-ref", "HEAD");
  }
}
