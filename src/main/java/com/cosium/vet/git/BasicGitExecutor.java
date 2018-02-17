package com.cosium.vet.git;

import com.cosium.vet.runtime.CommandRunner;
import org.apache.commons.lang3.ArrayUtils;

import static java.util.Objects.requireNonNull;

/**
 * Created on 17/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class BasicGitExecutor implements GitExecutor {

  private final CommandRunner commandRunner;

  BasicGitExecutor(CommandRunner commandRunner) {
    requireNonNull(commandRunner);
    this.commandRunner = commandRunner;
  }

  @Override
  public String execute(String... arguments) {
    return commandRunner.run(ArrayUtils.addAll(new String[]{"git"}, arguments));
  }
}
