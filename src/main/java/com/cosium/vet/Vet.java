package com.cosium.vet;

import com.cosium.vet.command.CompositeCommandArgParser;
import com.cosium.vet.command.DebugOptions;
import com.cosium.vet.command.VetAdvancedCommandArgParser;
import com.cosium.vet.command.VetCommandArgParser;
import com.cosium.vet.command.autocomplete.AutocompleteCommand;
import com.cosium.vet.command.autocomplete.AutocompleteCommandArgParser;
import com.cosium.vet.command.checkout.CheckoutCommand;
import com.cosium.vet.command.checkout.CheckoutCommandArgParser;
import com.cosium.vet.command.checkout.CheckoutCommandFactory;
import com.cosium.vet.command.checkout_new.CheckoutNewCommand;
import com.cosium.vet.command.checkout_new.CheckoutNewCommandArgParser;
import com.cosium.vet.command.checkout_new.CheckoutNewCommandFactory;
import com.cosium.vet.command.fire_and_forget.FireAndForgetCommand;
import com.cosium.vet.command.fire_and_forget.FireAndForgetCommandArgParser;
import com.cosium.vet.command.fire_and_forget.FireAndForgetCommandFactory;
import com.cosium.vet.command.new_.NewCommand;
import com.cosium.vet.command.new_.NewCommandArgParser;
import com.cosium.vet.command.new_.NewCommandFactory;
import com.cosium.vet.command.pull.PullCommand;
import com.cosium.vet.command.pull.PullCommandArgParser;
import com.cosium.vet.command.pull.PullCommandFactory;
import com.cosium.vet.command.push.PushCommand;
import com.cosium.vet.command.push.PushCommandArgParser;
import com.cosium.vet.command.push.PushCommandFactory;
import com.cosium.vet.command.status.StatusCommand;
import com.cosium.vet.command.status.StatusCommandArgParser;
import com.cosium.vet.command.status.StatusCommandFactory;
import com.cosium.vet.command.track.TrackCommand;
import com.cosium.vet.command.track.TrackCommandArgParser;
import com.cosium.vet.command.track.TrackCommandFactory;
import com.cosium.vet.command.untrack.UntrackCommand;
import com.cosium.vet.command.untrack.UntrackCommandArgParser;
import com.cosium.vet.command.untrack.UntrackCommandFactory;
import com.cosium.vet.gerrit.*;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitProvider;
import com.cosium.vet.runtime.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Created on 17/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class Vet {

  private static final String APP_NAME = "vet";

  private final NewCommandFactory newCommandFactory;
  private final CheckoutCommandFactory checkoutCommandFactory;
  private final CheckoutNewCommandFactory checkoutNewCommandFactory;
  private final PushCommandFactory pushCommandFactory;
  private final UntrackCommandFactory untrackCommandFactory;
  private final StatusCommandFactory statusCommandFactory;
  private final PullCommandFactory pullCommandFactory;
  private final FireAndForgetCommandFactory fireAndForgetCommandFactory;
  private final TrackCommandFactory trackCommandFactory;
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
    ChangeRepositoryFactory changeRepositoryFactory =
        new DefaultChangeRepositoryFactory(gitProvider, gitProvider);

    this.newCommandFactory = new NewCommand.Factory(changeRepositoryFactory, userInput, userOutput);
    this.checkoutCommandFactory =
        new CheckoutCommand.Factory(gitProvider, changeRepositoryFactory, userInput, userOutput);
    this.checkoutNewCommandFactory =
        new CheckoutNewCommand.Factory(gitProvider, changeRepositoryFactory, userInput, userOutput);
    this.pushCommandFactory = new PushCommand.Factory(changeRepositoryFactory, userOutput);
    this.untrackCommandFactory = new UntrackCommand.Factory(changeRepositoryFactory, userInput);
    this.statusCommandFactory =
        new StatusCommand.Factory(gitProvider, changeRepositoryFactory, userOutput);
    this.pullCommandFactory = new PullCommand.Factory(changeRepositoryFactory, userOutput);
    this.fireAndForgetCommandFactory =
        new FireAndForgetCommand.Factory(
            changeRepositoryFactory, gitProvider, userInput, userOutput);
    this.trackCommandFactory =
        new TrackCommand.Factory(changeRepositoryFactory, userInput, userOutput);

    List<VetAdvancedCommandArgParser> normalParsers =
        List.of(
            new CheckoutNewCommandArgParser(checkoutNewCommandFactory),
            new CheckoutCommandArgParser(checkoutCommandFactory),
            new PushCommandArgParser(pushCommandFactory),
            new FireAndForgetCommandArgParser(fireAndForgetCommandFactory),
            new NewCommandArgParser(newCommandFactory),
            new PullCommandArgParser(pullCommandFactory),
            new StatusCommandArgParser(statusCommandFactory),
            new TrackCommandArgParser(trackCommandFactory),
            new UntrackCommandArgParser(untrackCommandFactory));

    AutocompleteCommandArgParser autocompleteCommandArgParser =
        new AutocompleteCommandArgParser(
            new AutocompleteCommand.Factory(userOutput, normalParsers));

    List<VetAdvancedCommandArgParser> allParsers =
        Stream.concat(Stream.of(autocompleteCommandArgParser), normalParsers.stream())
            .collect(Collectors.toList());

    this.commandParser = new CompositeCommandArgParser(APP_NAME, allParsers, debugOptions);
  }

  public void run(String args[]) {
    commandParser.parse(args).execute();
  }

  public void new_(Boolean force, BranchShortName targetBranch) {
    newCommandFactory.build(force, targetBranch).execute();
  }

  public void fireAndForget(
      Boolean force, BranchShortName targetBranch, CodeReviewVote codeReviewVote) {
    fireAndForgetCommandFactory.build(force, targetBranch, codeReviewVote).execute();
  }

  public void checkoutNew(
      Boolean force, ChangeCheckoutBranchName checkoutBranch, BranchShortName targetbranch) {
    checkoutNewCommandFactory.build(force, checkoutBranch, targetbranch);
  }

  public void checkout(
      Boolean force,
      ChangeCheckoutBranchName checkoutBranch,
      ChangeNumericId numericId,
      BranchShortName targetbranch) {
    checkoutCommandFactory.build(force, checkoutBranch, numericId, targetbranch);
  }

  public void pull() {
    pullCommandFactory.build().execute();
  }

  /**
   * Executes the push command.
   *
   * @param publishDraftedComments True to publish drafted comments
   * @param workInProgress True to turn the change set to work in progress
   * @param patchSetSubject The optional patch set subject
   * @param bypassReview Submit directly the change, bypassing the review
   * @param codeReviewVote The code review vote
   */
  public void push(
      Boolean publishDraftedComments,
      Boolean workInProgress,
      PatchSubject patchSetSubject,
      Boolean bypassReview,
      CodeReviewVote codeReviewVote) {
    pushCommandFactory
        .build(
            publishDraftedComments, workInProgress, patchSetSubject, bypassReview, codeReviewVote)
        .execute();
  }

  public void track(Boolean force, ChangeNumericId numericId, BranchShortName targetbranch) {
    trackCommandFactory.build(force, numericId, targetbranch);
  }

  public void untrack(Boolean force) {
    untrackCommandFactory.build(force).execute();
  }

  public void status() {
    statusCommandFactory.build().execute();
  }
}
