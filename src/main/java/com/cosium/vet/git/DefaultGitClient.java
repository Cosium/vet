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
    return commandRunner.run(repositoryDirectory, "git", "merge-base", "HEAD", otherBranch);
  }

  @Override
  public String getTree() {
    return commandRunner.run(repositoryDirectory, "git", "rev-parse", "HEAD:");
  }

  @Override
  public String commitTree(String tree, String parent, String commitMessage) {
    return commandRunner.run(
        repositoryDirectory, "git", "commit-tree", tree, "-p", parent, "-m", commitMessage);
  }
}
