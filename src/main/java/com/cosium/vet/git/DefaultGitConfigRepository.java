package com.cosium.vet.git;

import com.cosium.vet.runtime.CommandRunner;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class DefaultGitConfigRepository implements GitConfigRepository {

  private final Path workingDirectory;
  private final CommandRunner commandRunner;
  private final GitClient gitClient;

  DefaultGitConfigRepository(
      Path workingDirectory, CommandRunner commandRunner, GitClient gitClient) {
    requireNonNull(workingDirectory);
    requireNonNull(commandRunner);
    requireNonNull(gitClient);
    this.workingDirectory = workingDirectory;
    this.commandRunner = commandRunner;
    this.gitClient = gitClient;
  }

  @Override
  public String getValue(String key) {
    return StringUtils.defaultIfBlank(
        commandRunner.run(workingDirectory, "git", "config", computeBranchKey(key)), null);
  }

  @Override
  public void setValue(String key, String value) {
    if (StringUtils.isBlank(value)) {
      commandRunner.run(workingDirectory, "git", "config", "--unset", computeBranchKey(key));
    } else {
      commandRunner.run(workingDirectory, "git", "config", computeBranchKey(key), value);
    }
  }

  private String computeBranchKey(String key) {
    String branch = gitClient.getBranchShortName();
    return String.format("branch.%s.%s", branch, key);
  }
}
