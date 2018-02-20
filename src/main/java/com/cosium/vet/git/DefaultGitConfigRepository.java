package com.cosium.vet.git;

import com.cosium.vet.runtime.CommandRunException;
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

  private final Path repositoryDirectory;
  private final CommandRunner commandRunner;

  DefaultGitConfigRepository(Path repositoryDirectory, CommandRunner commandRunner) {
    requireNonNull(repositoryDirectory);
    requireNonNull(commandRunner);
    this.repositoryDirectory = repositoryDirectory;
    this.commandRunner = commandRunner;
  }

  @Override
  public String getCurrentBranchValue(String key) {
    try {
      return StringUtils.defaultIfBlank(
          commandRunner.run(repositoryDirectory, "git", "config", computeBranchKey(key)), null);
    } catch (CommandRunException e) {
      if (e.getExitCode() != 1) {
        throw e;
      }
      return null;
    }
  }

  @Override
  public void setCurrentBranchValue(String key, String value) {
    if (StringUtils.isBlank(value)) {
      commandRunner.run(repositoryDirectory, "git", "config", "--unset", computeBranchKey(key));
    } else {
      commandRunner.run(repositoryDirectory, "git", "config", computeBranchKey(key), value);
    }
  }

  private String computeBranchKey(String key) {
    String branch = getBranchShortName();
    return String.format("branch.%s.%s", branch, key);
  }

  private String getBranchShortName() {
    return commandRunner.run(repositoryDirectory, "git", "rev-parse", "--abbrev-ref", "HEAD");
  }
}
