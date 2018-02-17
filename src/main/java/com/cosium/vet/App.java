package com.cosium.vet;

import com.cosium.vet.gerrit.DefaultGerritClientFactory;
import com.cosium.vet.gerrit.GerritClientFactory;
import com.cosium.vet.git.DefaultGitClientFactory;
import com.cosium.vet.git.GitClientFactory;
import com.cosium.vet.push.PushCommandArgParser;
import com.cosium.vet.runtime.BasicCommandRunner;
import com.cosium.vet.runtime.CommandRunner;
import com.google.common.collect.Lists;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

public class App {

  public static final String NAME = "vet";

  public static void main(String[] args) {
    CommandRunner commandRunner = new BasicCommandRunner();
    GitClientFactory gitClientFactory =
        new DefaultGitClientFactory(Paths.get(System.getProperty("user.dir")), commandRunner);
    GerritClientFactory gerritClientFactory = new DefaultGerritClientFactory(gitClientFactory);
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
}
