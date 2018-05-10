package com.cosium.vet.command.fire_and_forget;

import com.cosium.vet.command.VetAdvancedCommandArgParser;
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
public class FireAndForgetCommandArgParser implements VetAdvancedCommandArgParser {

  private static final String COMMAND_NAME = "fire-and-forget";

  private static final String FORCE = "f";
  private static final String CHANGE_TARGET_BRANCH = "t";

  private final FireAndForgetCommandFactory factory;
  private final Options options;

  public FireAndForgetCommandArgParser(FireAndForgetCommandFactory factory) {
    this.factory = requireNonNull(factory);
    options = new Options();
    options.addOption(
        Option.builder(FORCE)
            .numberOfArgs(0)
            .longOpt("force")
            .desc("Forces the execution of the command, bypassing any confirmation prompt.")
            .build());
    options.addOption(
        Option.builder(CHANGE_TARGET_BRANCH)
            .argName("branch")
            .longOpt("target-branch")
            .hasArg()
            .desc("The id of the change.")
            .build());
  }

  @Override
  public void displayHelp(String executableName) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(
        String.format("%s %s", executableName, COMMAND_NAME),
        StringUtils.EMPTY,
        options,
        "Creates a new untracked change with +2 then resets the current branch to the target branch. "
            + "Ideal for self voted quick fixes.",
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
      commandLine = parser.parse(options, args);
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
