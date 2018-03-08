package com.cosium.vet;

import com.cosium.vet.command.CompositeCommandArgParser;
import com.cosium.vet.command.DebugOptions;
import com.cosium.vet.command.VetCommandArgParser;
import com.cosium.vet.gerrit.DefaultGerritClientFactory;
import com.cosium.vet.gerrit.GerritClientFactory;
import com.cosium.vet.gerrit.PatchSetSubject;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitProvider;
import com.cosium.vet.push.PushCommand;
import com.cosium.vet.push.PushCommandArgParser;
import com.cosium.vet.push.PushCommandFactory;
import com.cosium.vet.runtime.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Created on 17/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class Vet {

  private static final String APP_NAME = "vet";

  private final PushCommandFactory pushCommandFactory;
  private final VetCommandArgParser commandParser;

  /**
   * @param interactive True if interactive mode (human interaction) should be enabled. False
   *     otherwise.
   */
  public Vet(boolean interactive) {
    this(interactive, DebugOptions.empty());
  }

  /**
   * @param interactive True if interactive mode (human interaction) should be enabled. False
   *     otherwise.
   * @param debugOptions The debug options to use
   */
  public Vet(boolean interactive, DebugOptions debugOptions) {
    this(
        Paths.get(System.getProperty("user.dir")),
        new BasicCommandRunner(),
        interactive,
        debugOptions);
  }

  /**
   * @param workingDir The working directory
   * @param commandRunner The command runner
   * @param interactive True if interactive mode (human interaction) should be enabled. False
   *     otherwise.
   */
  public Vet(
      Path workingDir,
      CommandRunner commandRunner,
      boolean interactive,
      DebugOptions debugOptions) {
    requireNonNull(workingDir);
    requireNonNull(commandRunner);
    requireNonNull(debugOptions);

    UserInput userInput;
    if (interactive) {
      userInput = new InteractiveUserInput();
    } else {
      userInput = new NonInteractiveUserInput();
    }

    GitProvider gitProvider = new GitProvider(workingDir, commandRunner);
    GerritClientFactory gerritClientFactory =
        new DefaultGerritClientFactory(gitProvider, gitProvider);
    this.pushCommandFactory = new PushCommand.Factory(gitProvider, gerritClientFactory, userInput);
    this.commandParser =
        new CompositeCommandArgParser(
            APP_NAME, List.of(new PushCommandArgParser(pushCommandFactory)), debugOptions);
  }

  public void run(String args[]) {
    commandParser.parse(args).execute();
  }

  /**
   * Executes the push command.
   *
   * @param targetBranch The optional target branch
   * @param publishDraftedComments True to publish drafted comments
   * @param patchSetSubject The optional patch set subject
   */
  public void push(
      BranchShortName targetBranch,
      Boolean publishDraftedComments,
      PatchSetSubject patchSetSubject) {
    pushCommandFactory.build(targetBranch, publishDraftedComments, patchSetSubject).execute();
  }
}
