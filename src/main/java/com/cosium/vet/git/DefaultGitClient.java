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

  private static final String GIT = "git";
  private final Path repositoryDirectory;
  private final CommandRunner commandRunner;
  private final GitConfigRepository gitConfigRepository;

  DefaultGitClient(
      Path repositoryDirectory,
      CommandRunner commandRunner,
      GitConfigRepository gitConfigRepository) {
    requireNonNull(repositoryDirectory);
    requireNonNull(commandRunner);
    requireNonNull(gitConfigRepository);
    this.repositoryDirectory = repositoryDirectory;
    this.commandRunner = commandRunner;
    this.gitConfigRepository = gitConfigRepository;
  }

  @Override
  public String getBranchRemote() {
    return gitConfigRepository.getCurrentBranchValue("remote");
  }

  @Override
  public String getBranchMerge() {
    return gitConfigRepository.getCurrentBranchValue("merge");
  }

  @Override
  public String getMostRecentCommonCommit(String otherBranch) {
    return commandRunner.run(repositoryDirectory, GIT, "merge-base", "HEAD", otherBranch);
  }

  @Override
  public String getTree() {
    return commandRunner.run(repositoryDirectory, GIT, "rev-parse", "HEAD:");
  }

  @Override
  public String commitTree(String tree, String parent, String commitMessage) {
    return commandRunner.run(
        repositoryDirectory, GIT, "commit-tree", tree, "-p", parent, "-m", commitMessage);
  }

  @Override
  public String writeTree() {
    return commandRunner.run(repositoryDirectory, GIT, "write-tree");
  }

  @Override
  public String revParse(String revision) {
    return commandRunner.run(repositoryDirectory, GIT, "rev-parse", revision);
  }

  @Override
  public String var(String varName) {
    return commandRunner.run(repositoryDirectory, GIT, "var", varName);
  }

  @Override
  public String hashObject(String type, String object) {
    return commandRunner.runWithStdIn(
        repositoryDirectory, object, GIT, "hash-object", "-t", type, "--stdin");
  }

  @Override
  public String getLastCommitMessage() {
    return commandRunner.run(repositoryDirectory, GIT, "log", "-1", "--pretty=%B");
  }

  @Override
  public void push(String remote, String refspec) {
    commandRunner.run(repositoryDirectory, GIT, "push", remote, refspec);
  }
}
