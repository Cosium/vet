package com.cosium.vet;

import com.cosium.vet.gerrit.DefaultGerritClientFactory;
import com.cosium.vet.gerrit.GerritClientFactory;
import com.cosium.vet.git.DefaultGitClientFactory;
import com.cosium.vet.git.GitClientFactory;
import com.cosium.vet.push.PushCommand;
import com.cosium.vet.push.PushCommandArgParser;
import com.cosium.vet.runtime.BasicCommandRunner;
import com.cosium.vet.runtime.CommandRunner;
import com.google.common.collect.Lists;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created on 17/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class Vet {

  private final Path workingDir;
  private final CommandRunner commandRunner;
  private final GitClientFactory gitClientFactory;
  private final GerritClientFactory gerritClientFactory;

  public Vet() {
    this(false);
  }

  public Vet(boolean useDockerGit) {
    workingDir = Paths.get(System.getProperty("user.dir"));
    commandRunner = new BasicCommandRunner();
    gitClientFactory = new DefaultGitClientFactory(workingDir, commandRunner, useDockerGit);
    gerritClientFactory = new DefaultGerritClientFactory(gitClientFactory);
  }

  public void run(String args[]) {
    PushCommandArgParser pushCommandArgParser =
        new PushCommandArgParser(gitClientFactory, gerritClientFactory);
    Lists.newArrayList(pushCommandArgParser)
        .stream()
        .map(commandParser -> commandParser.parse(Arrays.copyOf(args, args.length)))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .orElseGet(HelpCommand::new)
        .execute();
  }

  public void push(String targetBranch) {
    new PushCommand(gitClientFactory, gerritClientFactory, targetBranch).execute();
  }

  public void help() {
    new HelpCommand().execute();
  }
}
