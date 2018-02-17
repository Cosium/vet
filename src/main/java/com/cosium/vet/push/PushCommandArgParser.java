package com.cosium.vet.push;

import com.cosium.vet.VetCommand;
import com.cosium.vet.VetCommandArgParser;
import com.cosium.vet.gerrit.GerritClientFactory;
import com.cosium.vet.git.GitClientFactory;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Created on 14/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class PushCommandArgParser implements VetCommandArgParser {

  private static final Logger LOG = LoggerFactory.getLogger(PushCommandArgParser.class);
  private static final String COMMAND_NAME = "push";
  private static final String BRANCH_OPTION = "b";

  private final GerritClientFactory gerritClientFactory;
  private final GitClientFactory gitRepositoryProvider;

  public PushCommandArgParser(
          GitClientFactory gitRepositoryProvider, GerritClientFactory gerritClientFactory) {
    requireNonNull(gerritClientFactory);
    requireNonNull(gitRepositoryProvider);
    this.gerritClientFactory = gerritClientFactory;
    this.gitRepositoryProvider = gitRepositoryProvider;
  }

  @Override
  public Optional<VetCommand> parse(String[] args) {
    if (args.length == 0) {
      LOG.trace("Argument array is empty. Not a push command.");
      return Optional.empty();
    }
    if (!COMMAND_NAME.equals(args[0])) {
      LOG.trace("First argument doesn't match {}. Not a push command.", COMMAND_NAME);
      return Optional.empty();
    }
    LOG.trace("This is a push command");

    Options options = new Options();
    options.addOption(
        Option.builder(BRANCH_OPTION)
            .argName("branch-name")
            .longOpt("branch")
            .hasArg()
            .desc(
                "The branch targeted by the changes. This can only be set when initializing the change set.")
            .build());

    CommandLineParser parser = new DefaultParser();
    try {
      parser.parse(options, args);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }

    return Optional.of(
        new PushCommand(
            gitRepositoryProvider,
            gerritClientFactory,
            options.getOption(BRANCH_OPTION).getValue()));
  }
}
