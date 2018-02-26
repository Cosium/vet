package com.cosium.vet.git;

import com.cosium.vet.runtime.CommandRunException;
import com.cosium.vet.runtime.CommandRunner;
import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class DefaultGitConfigRepository implements GitConfigRepository {

  private static final String GIT = "git";

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
    return getValue(computeBranchKey(key));
  }

  @Override
  public void setCurrentBranchValue(String key, String value) {
    if (StringUtils.isBlank(value)) {
      runIgnoringExitCode(5, GIT, "config", "--unset", computeBranchKey(key));
    } else {
      commandRunner.run(repositoryDirectory, GIT, "config", computeBranchKey(key), value);
    }
  }

  @Override
  public String getValue(String key) {
    return runIgnoringExitCode(1, GIT, "config", key);
  }

  private String runIgnoringExitCode(int exitCodeToIgnore, String... command) {
    try {
      return commandRunner.run(repositoryDirectory, command);
    } catch (CommandRunException e) {
      if (exitCodeToIgnore != e.getExitCode()) {
        throw e;
      }
      return null;
    }
  }

  private String computeBranchKey(String key) {
    String branch = getBranchShortName();
    return String.format("branch.%s.%s", branch, key);
  }

  private String getBranchShortName() {
    return commandRunner.run(repositoryDirectory, GIT, "rev-parse", "--abbrev-ref", "HEAD");
  }
}
