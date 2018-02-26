package com.cosium.vet.help;

import com.cosium.vet.VetCommand;
import com.cosium.vet.VetCommandArgParser;
import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Created on 14/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class HelpCommand implements VetCommand {

  private final String executableName;
  private final List<VetCommandArgParser> availableParsers;
  private final String commandToDescribe;

  /**
   * @param executableName The name of the current executable
   * @param availableParsers The available command parsers
   * @param commandToDescribe The optional command to describe
   */
  public HelpCommand(
      String executableName, List<VetCommandArgParser> availableParsers, String commandToDescribe) {
    if (StringUtils.isBlank(executableName)) {
      throw new IllegalArgumentException("appName can't be blank");
    }
    requireNonNull(availableParsers);
    this.executableName = executableName;
    this.availableParsers = availableParsers;
    this.commandToDescribe = commandToDescribe;
  }

  @Override
  public void execute() {
    if (StringUtils.isNotBlank(commandToDescribe)) {
      Optional<VetCommandArgParser> parser =
          availableParsers
              .stream()
              .filter(p -> commandToDescribe.equals(p.getCommandArgName()))
              .findFirst();
      if (parser.isPresent()) {
        parser.get().displayHelp(executableName);
        return;
      }
    }

    String builder =
        "usage: "
            + executableName
            + " <command>\n\n"
            + "where <command> is one of:\n"
            + " "
            + availableParsers
                .stream()
                .map(VetCommandArgParser::getCommandArgName)
                .distinct()
                .collect(Collectors.joining(", "))
            + "\n\n"
            + executableName
            + " <command> -h,--help   Display help on <command>";

    System.out.println(builder);
  }

  public static class ArgParser implements VetCommandArgParser {

    private final String executableName;
    private final List<VetCommandArgParser> availableParsers;

    public ArgParser(String executableName, List<VetCommandArgParser> availableParsers) {
      if (StringUtils.isBlank(executableName)) {
        throw new IllegalArgumentException("appName can't be blank");
      }
      requireNonNull(availableParsers);
      this.executableName = executableName;
      this.availableParsers = availableParsers;
    }

    @Override
    public void displayHelp(String executableName) {}

    @Override
    public String getCommandArgName() {
      return "help";
    }

    @Override
    public boolean canParse(String... args) {
      return Arrays.stream(args).anyMatch(arg -> arg.equals("--help") || arg.equals("-h"));
    }

    @Override
    public VetCommand parse(String... args) {
      String commandToDescribe = null;
      if (args.length > 0) {
        commandToDescribe = args[0];
      }
      return new HelpCommand(executableName, availableParsers, commandToDescribe);
    }
  }
}
