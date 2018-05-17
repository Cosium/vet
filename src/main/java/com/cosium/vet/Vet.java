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
import com.cosium.vet.gerrit.Change;
import com.cosium.vet.gerrit.ChangeNumericId;
import com.cosium.vet.gerrit.ChangeRepository;
import com.cosium.vet.gerrit.DefaultChangeRepositoryFactory;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.GitProvider;
import com.cosium.vet.runtime.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

  private final GitClient git;
  private final ChangeRepository changeRepository;

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

  public Vet(boolean interactive) {
    this(interactive, DebugOptions.empty());
  }

  public Vet(boolean interactive, DebugOptions debugOptions) {
    this(interactive, debugOptions, Paths.get(System.getProperty("user.dir")));
  }

  public Vet(boolean interactive, DebugOptions debugOptions, Path workingDir) {
    this(interactive, debugOptions, workingDir, new BasicCommandRunner());
  }

  /**
   * @param interactive True if interactive mode (human interaction) should be enabled. False
   *     otherwise.
   * @param debugOptions Th debug options to use
   * @param workingDir The working directory
   * @param commandRunner The command runner
   */
  public Vet(
      boolean interactive,
      DebugOptions debugOptions,
      Path workingDir,
      CommandRunner commandRunner) {
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
    git = gitProvider.build();
    changeRepository = new DefaultChangeRepositoryFactory(gitProvider, git).build();

    this.newCommandFactory = new NewCommand.Factory(changeRepository, userInput, userOutput);
    this.checkoutCommandFactory =
        new CheckoutCommand.Factory(git, changeRepository, userInput, userOutput);
    this.checkoutNewCommandFactory =
        new CheckoutNewCommand.Factory(git, changeRepository, userInput, userOutput);
    this.pushCommandFactory = new PushCommand.Factory(changeRepository, userOutput);
    this.untrackCommandFactory = new UntrackCommand.Factory(changeRepository, userInput);
    this.statusCommandFactory = new StatusCommand.Factory(git, changeRepository, userOutput);
    this.pullCommandFactory = new PullCommand.Factory(changeRepository, userOutput);
    this.fireAndForgetCommandFactory =
        new FireAndForgetCommand.Factory(git, changeRepository, userInput, userOutput);
    this.trackCommandFactory = new TrackCommand.Factory(changeRepository, userInput, userOutput);

    List<VetAdvancedCommandArgParser> normalParsers =
        Arrays.asList(
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

  public Optional<Change> getTrackedChange() {
    return changeRepository.getTrackedChange();
  }

  public boolean isChangeExist(ChangeNumericId changeNumericId) {
    return changeRepository.exists(changeNumericId);
  }

  public PushCommandFactory pushCommandFactory() {
    return pushCommandFactory;
  }

  public NewCommandFactory newCommandFactory() {
    return newCommandFactory;
  }

  public CheckoutNewCommandFactory checkoutNewCommandFactory() {
    return checkoutNewCommandFactory;
  }

  public FireAndForgetCommandFactory fireAndForgetCommandFactory() {
    return fireAndForgetCommandFactory;
  }

  public TrackCommandFactory trackCommandFactory() {
    return trackCommandFactory;
  }
}
