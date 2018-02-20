package com.cosium.vet.push;

import com.cosium.vet.VetCommand;
import com.cosium.vet.VetCommandArgParser;
import com.cosium.vet.gerrit.GerritClientFactory;
import com.cosium.vet.git.GitClientFactory;
import com.cosium.vet.runtime.UserInput;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * Created on 14/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class PushCommandArgParser implements VetCommandArgParser {

  private static final Logger LOG = LoggerFactory.getLogger(PushCommandArgParser.class);
  private static final String COMMAND_NAME = "push";
  private static final String REMOTE_OPTION = "r";
  private static final String BRANCH_OPTION = "b";
  private static final String CHANGE_DESCRIPTION = "d";

  private final GerritClientFactory gerritClientFactory;
  private final GitClientFactory gitRepositoryFactory;
  private final UserInput userInput;

  public PushCommandArgParser(
      GitClientFactory gitRepositoryFactory,
      GerritClientFactory gerritClientFactory,
      UserInput userInput) {
    requireNonNull(gerritClientFactory);
    requireNonNull(gitRepositoryFactory);
    requireNonNull(userInput);
    this.gerritClientFactory = gerritClientFactory;
    this.gitRepositoryFactory = gitRepositoryFactory;
    this.userInput = userInput;
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
            .argName("remote-name")
            .longOpt("remote")
            .hasArg()
            .desc("The remote macthing the targeted Gerrit site. Default value is 'origin'.")
            .build());
    options.addOption(
        Option.builder(BRANCH_OPTION)
            .argName("branch-name")
            .longOpt("branch")
            .hasArg()
            .desc("The branch targeted by the changes. Default value is 'master'.")
            .build());
    options.addOption(
        Option.builder(CHANGE_DESCRIPTION)
            .argName("change-description")
            .longOpt("change-description")
            .hasArg()
            .desc("The description of the change")
            .build());

    CommandLineParser parser = new DefaultParser();
    try {
      parser.parse(options, args);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }

    RemoteName remote =
        ofNullable(options.getOption(REMOTE_OPTION).getValue())
            .filter(StringUtils::isNotBlank)
            .map(RemoteName::of)
            .orElse(null);
    BranchShortName targetBranch =
        ofNullable(options.getOption(BRANCH_OPTION).getValue())
            .filter(StringUtils::isNotBlank)
            .map(BranchShortName::of)
            .orElse(null);
    ChangeDescription changeDescription =
        ofNullable(options.getOption(CHANGE_DESCRIPTION).getValue())
            .filter(StringUtils::isNotBlank)
            .map(ChangeDescription::of)
            .orElse(null);

    return Optional.of(
        new PushCommand(
            gitRepositoryFactory.build(),
            gerritClientFactory.build(),
            userInput,
            remote,
            targetBranch,
            changeDescription));
  }
}
