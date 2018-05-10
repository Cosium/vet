package com.cosium.vet.command.checkout;

import com.cosium.vet.command.VetAdvancedCommandArgParser;
import com.cosium.vet.command.VetCommand;
import com.cosium.vet.gerrit.ChangeCheckoutBranchName;
import com.cosium.vet.gerrit.ChangeNumericId;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.thirdparty.apache_commons_cli.*;
import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;
import com.cosium.vet.thirdparty.apache_commons_lang3.math.NumberUtils;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * Created on 09/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class CheckoutCommandArgParser implements VetAdvancedCommandArgParser {

  private static final String COMMAND_NAME = "checkout";

  private static final String FORCE = "f";
  private static final String CHANGE_NUMERIC_ID = "i";
  private static final String CHANGE_TARGET_BRANCH = "t";
  private static final String CHECKOUT_BRANCH = "b";

  private final CheckoutCommandFactory factory;
  private final Options options;

  public CheckoutCommandArgParser(CheckoutCommandFactory factory) {
    this.factory = requireNonNull(factory);
    options = new Options();
    options.addOption(
        Option.builder(FORCE)
            .numberOfArgs(0)
            .longOpt("force")
            .hasArg()
            .desc("Forces the execution of the command, bypassing any confirmation prompt.")
            .build());
    options.addOption(
        Option.builder(CHANGE_NUMERIC_ID)
            .argName("id")
            .longOpt("numeric-id")
            .hasArg()
            .desc("The numeric id of the change.")
            .build());
    options.addOption(
        Option.builder(CHANGE_TARGET_BRANCH)
            .argName("branch")
            .longOpt("target-branch")
            .hasArg()
            .desc("The target branch of the change.")
            .build());
    options.addOption(
        Option.builder(CHECKOUT_BRANCH)
            .argName("branch")
            .longOpt("checkout-branch")
            .hasArg()
            .desc("The branch that will track the change.")
            .build());
  }

  @Override
  public void displayHelp(String executableName) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(
        String.format("%s %s", executableName, COMMAND_NAME),
        StringUtils.EMPTY,
        options,
        "Track an existing change from a new branch",
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
    ChangeNumericId numericId =
        ofNullable(commandLine.getOptionValue(CHANGE_NUMERIC_ID))
            .filter(NumberUtils::isDigits)
            .map(Long::parseLong)
            .map(ChangeNumericId::of)
            .orElse(null);

    BranchShortName targetBranch =
        ofNullable(commandLine.getOptionValue(CHANGE_TARGET_BRANCH))
            .filter(StringUtils::isNotBlank)
            .map(BranchShortName::of)
            .orElse(null);

    ChangeCheckoutBranchName checkoutBranch =
        ofNullable(commandLine.getOptionValue(CHECKOUT_BRANCH))
            .filter(StringUtils::isNotBlank)
            .map(ChangeCheckoutBranchName::of)
            .orElse(null);

    return factory.build(force, checkoutBranch, numericId, targetBranch);
  }
}
