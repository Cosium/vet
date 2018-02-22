package com.cosium.vet.push;

import com.cosium.vet.VetCommand;
import com.cosium.vet.VetCommandArgParser;
import com.cosium.vet.gerrit.ChangeSubject;
import com.cosium.vet.gerrit.GerritClientFactory;
import com.cosium.vet.git.BranchShortName;
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
  private static final String TARGET_BRANCH = "b";
  private static final String CHANGE_SUBJECT = "s";

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
        Option.builder(TARGET_BRANCH)
            .argName("branch-name")
            .longOpt("target-branch")
            .hasArg()
            .desc("The branch targeted by the changes. Default value is 'master'.")
            .build());
    options.addOption(
        Option.builder(CHANGE_SUBJECT)
            .argName("subject")
            .longOpt("change-subject")
            .hasArg()
            .desc("The subject of the change")
            .build());

    CommandLineParser parser = new DefaultParser();
    try {
      parser.parse(options, args);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }

    BranchShortName targetBranch =
        ofNullable(options.getOption(TARGET_BRANCH).getValue())
            .filter(StringUtils::isNotBlank)
            .map(BranchShortName::of)
            .orElse(null);
    ChangeSubject changeSubject =
        ofNullable(options.getOption(CHANGE_SUBJECT).getValue())
            .filter(StringUtils::isNotBlank)
            .map(ChangeSubject::of)
            .orElse(null);

    return Optional.of(
        new PushCommand(
            gitRepositoryFactory.build(),
            gerritClientFactory.build(null, null),
            userInput,
                changeSubject, targetBranch
        ));
  }
}
