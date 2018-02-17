package com.cosium.vet.git;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class DefaultGitClient implements GitClient {

  private final Path directory;
  private final GitExecutor git;

  DefaultGitClient(Path directory, GitExecutor gitExecutor) {
    requireNonNull(directory);
    requireNonNull(gitExecutor);
    this.directory = directory;
    this.git = gitExecutor;
  }

  @Override
  public String getBranchRemote() {
    String branchShortName = getBranchShortName();
    return git.execute(directory, "config", String.format("branch.%s.remote", branchShortName));
  }

  @Override
  public String getBranchMerge() {
    String branchShortName = getBranchShortName();
    return git.execute(directory, "config", String.format("branch.%s.merge", branchShortName));
  }

  @Override
  public String getMostRecentCommonCommit(String otherBranch) {
    return git.execute(directory, "merge-base", "HEAD", otherBranch);
  }

  @Override
  public String getTree() {
    return git.execute(directory, "rev-parse", "HEAD:");
  }

  @Override
  public String commitTree(String tree, String parent, String commitMessage) {
    return git.execute(directory, "commit-tree", tree, "-p", parent, "-m", commitMessage);
  }

  private String getBranchShortName() {
    return git.execute(directory, "rev-parse", "--abbrev-ref", "HEAD");
  }
}
