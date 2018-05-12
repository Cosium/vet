package com.cosium.vet.command.checkout_new;

import com.cosium.vet.command.AbstractVetAdvancedCommandArgParser;
import com.cosium.vet.command.VetCommand;
import com.cosium.vet.gerrit.ChangeCheckoutBranchName;
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
public class CheckoutNewCommandArgParser extends AbstractVetAdvancedCommandArgParser {

  private static final String COMMAND_NAME = "checkout-new";

  private static final String FORCE = "f";
  private static final String CHECKOUT_BRANCH = "b";

  private final CheckoutNewCommandFactory factory;

  public CheckoutNewCommandArgParser(CheckoutNewCommandFactory factory) {
    super(
        new Options()
            .addOption(
                Option.builder(FORCE)
                    .numberOfArgs(0)
                    .longOpt("force")
                    .desc("Forces the execution of the command, bypassing any confirmation prompt.")
                    .build())
            .addOption(
                Option.builder(CHECKOUT_BRANCH)
                    .argName("branch")
                    .longOpt("checkout-branch")
                    .hasArg()
                    .desc("The branch that will be created to track the change.")
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
        "Creates a new change and tracks it from a new branch",
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

    ChangeCheckoutBranchName checkoutBranch =
        ofNullable(commandLine.getOptionValue(CHECKOUT_BRANCH))
            .filter(StringUtils::isNotBlank)
            .map(ChangeCheckoutBranchName::of)
            .orElse(null);

    return factory.build(force, checkoutBranch);
  }
}
