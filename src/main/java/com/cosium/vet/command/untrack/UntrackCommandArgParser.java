package com.cosium.vet.command.untrack;

import com.cosium.vet.command.VetAdvancedCommandArgParser;
import com.cosium.vet.command.VetCommand;
import com.cosium.vet.thirdparty.apache_commons_cli.*;
import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;

/**
 * Created on 09/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class UntrackCommandArgParser implements VetAdvancedCommandArgParser {

  private static final String COMMAND_NAME = "untrack";

  private static final String FORCE = "f";

  private final UntrackCommandFactory factory;
  private final Options options;

  public UntrackCommandArgParser(UntrackCommandFactory factory) {
    this.factory = requireNonNull(factory);
    options = new Options();
    options.addOption(
        Option.builder(FORCE)
            .numberOfArgs(0)
            .longOpt("force")
            .hasArg()
            .desc("Forces the execution of the command, bypassing any confirmation prompt.")
            .build());
  }

  @Override
  public void displayHelp(String executableName) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(
        String.format("%s %s", executableName, COMMAND_NAME),
        StringUtils.EMPTY,
        options,
        "Untrack any tracked change",
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
    return factory.build(force);
  }
}
