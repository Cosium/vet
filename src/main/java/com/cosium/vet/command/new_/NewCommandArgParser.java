package com.cosium.vet.command.new_;

import com.cosium.vet.command.AbstractVetAdvancedCommandArgParser;
import com.cosium.vet.command.VetCommand;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.thirdparty.apache_commons_cli.*;
import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * Created on 09/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class NewCommandArgParser extends AbstractVetAdvancedCommandArgParser {

  private static final String COMMAND_NAME = "new";

  private static final String FORCE = "f";
  private static final String CHANGE_TARGET_BRANCH = "t";

  private final NewCommandFactory factory;

  public NewCommandArgParser(NewCommandFactory factory) {
    super(
        new Options()
            .addOption(
                Option.builder(FORCE)
                    .numberOfArgs(0)
                    .longOpt("force")
                    .desc("Forces the execution of the command, bypassing any confirmation prompt.")
                    .build())
            .addOption(
                Option.builder(CHANGE_TARGET_BRANCH)
                    .argName("branch")
                    .longOpt("target-branch")
                    .hasArg()
                    .desc("The change target branch.")
                    .build()));
    this.factory = requireNonNull(factory);
  }

  @Override
  public void displayHelp(String executableName) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(
        String.format("%s %s", executableName, COMMAND_NAME),
        StringUtils.EMPTY,
        getOptions(),
        "Creates a new change and tracks it from the current branch.",
        true);
  }

  @Override
  public String getCommandArgName() {
    return COMMAND_NAME;
  }

  @Override
  public boolean canParse(String... args) {
    return Arrays.stream(args).anyMatch(COMMAND_NAME::equals);
  }

  @Override
  public VetCommand parse(String... args) {
    CommandLineParser parser = new DefaultParser();
    CommandLine commandLine;
    try {
      commandLine = parser.parse(getOptions(), args);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }

    Boolean force = commandLine.hasOption(FORCE) ? true : null;
    BranchShortName targetBranch =
        ofNullable(commandLine.getOptionValue(CHANGE_TARGET_BRANCH))
            .filter(StringUtils::isNotBlank)
            .map(BranchShortName::of)
            .orElse(null);

    return factory.build(force, targetBranch);
  }
}
