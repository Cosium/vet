package com.cosium.vet;

import com.cosium.vet.command.CompositeCommandArgParser;
import com.cosium.vet.command.DebugOptions;
import com.cosium.vet.command.VetCommandArgParser;
import com.cosium.vet.command.push.PushCommand;
import com.cosium.vet.command.push.PushCommandArgParser;
import com.cosium.vet.command.push.PushCommandFactory;
import com.cosium.vet.command.track.TrackCommand;
import com.cosium.vet.command.track.TrackCommandArgParser;
import com.cosium.vet.command.track.TrackCommandFactory;
import com.cosium.vet.command.untrack.UntrackCommand;
import com.cosium.vet.command.untrack.UntrackCommandArgParser;
import com.cosium.vet.command.untrack.UntrackCommandFactory;
import com.cosium.vet.gerrit.ChangeNumericId;
import com.cosium.vet.gerrit.DefaultGerritChangeRepositoryFactory;
import com.cosium.vet.gerrit.GerritChangeRepositoryFactory;
import com.cosium.vet.gerrit.PatchSetSubject;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitProvider;
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
  private final TrackCommandFactory trackCommandFactory;
  private final UntrackCommandFactory untrackCommandFactory;
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

    UserOutput userOutput = new DefaultUserOutput();

    UserInput userInput;
    if (interactive) {
      userInput = new InteractiveUserInput(userOutput);
    } else {
      userInput = new NonInteractiveUserInput();
    }

    GitProvider gitProvider = new GitProvider(workingDir, commandRunner);
    GerritChangeRepositoryFactory changeRepositoryFactory =
        new DefaultGerritChangeRepositoryFactory(gitProvider, gitProvider);
    this.trackCommandFactory =
        new TrackCommand.Factory(changeRepositoryFactory, userInput, userOutput);
    this.pushCommandFactory =
        new PushCommand.Factory(gitProvider, changeRepositoryFactory, userOutput);
    this.untrackCommandFactory = new UntrackCommand.Factory(changeRepositoryFactory, userInput);

    this.commandParser =
        new CompositeCommandArgParser(
            APP_NAME,
            List.of(
                new TrackCommandArgParser(trackCommandFactory),
                new PushCommandArgParser(pushCommandFactory),
                new UntrackCommandArgParser(untrackCommandFactory)),
            debugOptions);
  }

  public void run(String args[]) {
    commandParser.parse(args).execute();
  }

  public void track(Boolean force, ChangeNumericId numericId, BranchShortName targetBranch) {
    trackCommandFactory.build(force, numericId, targetBranch).execute();
  }

  public void untrack(Boolean force) {
    untrackCommandFactory.build(force).execute();
  }

  /**
   * Executes the push command.
   *
   * @param publishDraftedComments True to publish drafted comments
   * @param workInProgress True to turn the change set to work in progress
   * @param patchSetSubject The optional patch set subject
   * @param bypassReview Submit directly the change, bypassing the review
   */
  public void push(
      Boolean publishDraftedComments,
      Boolean workInProgress,
      PatchSetSubject patchSetSubject,
      Boolean bypassReview) {
    pushCommandFactory
        .build(publishDraftedComments, workInProgress, patchSetSubject, bypassReview)
        .execute();
  }
}
