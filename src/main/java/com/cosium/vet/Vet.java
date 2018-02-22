package com.cosium.vet;

import com.cosium.vet.file.DefaultFileSystem;
import com.cosium.vet.file.FileSystem;
import com.cosium.vet.gerrit.*;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitClientFactory;
import com.cosium.vet.git.GitProvider;
import com.cosium.vet.push.PushCommand;
import com.cosium.vet.push.PushCommandArgParser;
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

  private final GitClientFactory gitClientFactory;
  private final GerritClientFactory gerritClientFactory;
  private final UserInput userInput;

  public Vet() {
    this(
        Paths.get(System.getProperty("user.dir")),
        new InteractiveUserInput(),
        new BasicCommandRunner());
  }

  public Vet(Path workingDir, UserInput userInput, CommandRunner commandRunner) {
    this(workingDir, userInput, commandRunner, new DefaultFileSystem());
  }

  public Vet(
      Path workingDir, UserInput userInput, CommandRunner commandRunner, FileSystem fileSystem) {
    requireNonNull(workingDir);
    requireNonNull(userInput);
    requireNonNull(commandRunner);
    requireNonNull(fileSystem);

    GitProvider gitProvider = new GitProvider(workingDir, commandRunner);
    this.userInput = userInput;
    this.gitClientFactory = gitProvider;
    this.gerritClientFactory =
        new DefaultGerritClientFactory(fileSystem, gitProvider, gitClientFactory, userInput);
  }

  public void run(String args[]) {
    PushCommandArgParser pushCommandArgParser =
        new PushCommandArgParser(gitClientFactory, gerritClientFactory, userInput);
    Lists.newArrayList(pushCommandArgParser)
        .stream()
        .map(commandParser -> commandParser.parse(Arrays.copyOf(args, args.length)))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .orElseGet(HelpCommand::new)
        .execute();
  }

  public void push(
      GerritUser user,
      GerritPassword password,
      BranchShortName targetBranch,
      ChangeSubject changeSubject) {
    new PushCommand(
            gitClientFactory.build(),
            gerritClientFactory.build(user, password),
            userInput,
            targetBranch,
            changeSubject)
        .execute();
  }

  public void help() {
    new HelpCommand().execute();
  }
}
