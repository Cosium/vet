package com.cosium.vet;

import com.cosium.vet.gerrit.DefaultGerritClientFactory;
import com.cosium.vet.gerrit.GerritClientFactory;
import com.cosium.vet.gerrit.PatchSetSubject;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitProvider;
import com.cosium.vet.help.HelpCommand;
import com.cosium.vet.push.PushCommand;
import com.cosium.vet.push.PushCommandArgParser;
import com.cosium.vet.push.PushCommandFactory;
import com.cosium.vet.runtime.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
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
  private final List<VetCommandArgParser> commandParsers;
  private final VetCommandArgParser helpCommandParser;

  public Vet(boolean interactive) {
    this(Paths.get(System.getProperty("user.dir")), new BasicCommandRunner(), interactive);
  }

  public Vet(Path workingDir, CommandRunner commandRunner, boolean interactive) {
    requireNonNull(workingDir);
    requireNonNull(commandRunner);

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

    List<VetCommandArgParser> nonHelpParsers =
        List.of(new PushCommandArgParser(pushCommandFactory));
    this.helpCommandParser = new HelpCommand.ArgParser(APP_NAME, nonHelpParsers);
    List<VetCommandArgParser> parsers = new ArrayList<>();
    parsers.add(helpCommandParser);
    parsers.addAll(nonHelpParsers);
    this.commandParsers = Collections.unmodifiableList(parsers);
  }

  public void run(String args[]) {
    commandParsers
        .stream()
        .filter(p -> p.canParse(args))
        .findFirst()
        .orElse(helpCommandParser)
        .parse(args)
        .execute();
  }

  public void push(BranchShortName targetBranch, PatchSetSubject patchSetSubject) {
    pushCommandFactory.build(targetBranch, patchSetSubject).execute();
  }

  public void help(String commandToDescribe) {
    new HelpCommand(APP_NAME, commandParsers, commandToDescribe).execute();
  }
}
