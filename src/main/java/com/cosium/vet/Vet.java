package com.cosium.vet;

import com.cosium.vet.gerrit.DefaultGerritClientFactory;
import com.cosium.vet.gerrit.GerritClientFactory;
import com.cosium.vet.gerrit.PatchSetSubject;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitProvider;
import com.cosium.vet.push.PushCommand;
import com.cosium.vet.push.PushCommandArgParser;
import com.cosium.vet.push.PushCommandFactory;
import com.cosium.vet.runtime.BasicCommandRunner;
import com.cosium.vet.runtime.CommandRunner;
import com.cosium.vet.runtime.InteractiveUserInput;
import com.cosium.vet.runtime.UserInput;
import com.google.common.collect.Lists;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Created on 17/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class Vet {

  private final PushCommandFactory pushCommandFactory;

  public Vet() {
    this(
        Paths.get(System.getProperty("user.dir")),
        new InteractiveUserInput(),
        new BasicCommandRunner());
  }

  public Vet(Path workingDir, UserInput userInput, CommandRunner commandRunner) {
    requireNonNull(workingDir);
    requireNonNull(userInput);
    requireNonNull(commandRunner);

    GitProvider gitProvider = new GitProvider(workingDir, commandRunner);
    GerritClientFactory gerritClientFactory =
        new DefaultGerritClientFactory(gitProvider, gitProvider);
    this.pushCommandFactory = new PushCommand.Factory(gitProvider, gerritClientFactory, userInput);
  }

  public void run(String args[]) {
    PushCommandArgParser pushCommandArgParser = new PushCommandArgParser(pushCommandFactory);
    Lists.newArrayList(pushCommandArgParser)
        .stream()
        .map(commandParser -> commandParser.parse(Arrays.copyOf(args, args.length)))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .orElseGet(HelpCommand::new)
        .execute();
  }

  public void push(BranchShortName targetBranch, PatchSetSubject patchSetSubject) {
    pushCommandFactory.build(targetBranch, patchSetSubject).execute();
  }

  public void help() {
    new HelpCommand().execute();
  }
}
