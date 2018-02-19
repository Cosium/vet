package com.cosium.vet.git;

import com.cosium.vet.runtime.CommandRunner;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class DefaultGitClient implements GitClient {

  private final Path directory;
  private final CommandRunner commandRunner;

  DefaultGitClient(Path directory, CommandRunner commandRunner) {
    requireNonNull(directory);
    requireNonNull(commandRunner);
    this.directory = directory;
    this.commandRunner = commandRunner;
  }

  @Override
  public String getBranchRemote() {
    String branchShortName = getBranchShortName();
    return commandRunner.run(
        directory, "git", "config", String.format("branch.%s.remote", branchShortName));
  }

  @Override
  public String getBranchMerge() {
    String branchShortName = getBranchShortName();
    return commandRunner.run(
        directory, "git", "config", String.format("branch.%s.merge", branchShortName));
  }

  @Override
  public String getMostRecentCommonCommit(String otherBranch) {
    return commandRunner.run(directory, "git", "merge-base", "HEAD", otherBranch);
  }

  @Override
  public String getTree() {
    return commandRunner.run(directory, "git", "rev-parse", "HEAD:");
  }

  @Override
  public String commitTree(String tree, String parent, String commitMessage) {
    return commandRunner.run(
        directory, "git", "commit-tree", tree, "-p", parent, "-m", commitMessage);
  }

  private String getBranchShortName() {
    return commandRunner.run(directory, "git", "rev-parse", "--abbrev-ref", "HEAD");
  }
}
