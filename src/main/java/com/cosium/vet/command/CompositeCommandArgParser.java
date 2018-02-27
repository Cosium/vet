package com.cosium.vet.command;

import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;
import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Created on 26/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class CompositeCommandArgParser implements VetCommandArgParser {

  private static final Logger LOG = LoggerFactory.getLogger(CompositeCommandArgParser.class);
  private static final List<String> HELP_ARGS = List.of("-h", "--help");

  private final String executableName;
  private final List<VetAdvancedCommandArgParser> availableParsers;
  private final DebugOptions debugOptions;

  public CompositeCommandArgParser(
      String executableName,
      List<VetAdvancedCommandArgParser> availableParsers,
      DebugOptions debugOptions) {
    if (StringUtils.isBlank(executableName)) {
      throw new IllegalArgumentException("appName can't be blank");
    }
    requireNonNull(availableParsers);
    requireNonNull(debugOptions);
    this.executableName = executableName;
    this.availableParsers = availableParsers;
    this.debugOptions = debugOptions;
  }

  @Override
  public VetCommand parse(String... args) {
    boolean isHelp = Arrays.stream(args).anyMatch(HELP_ARGS::contains);

    Optional<VetAdvancedCommandArgParser> parser =
        availableParsers.stream().filter(p -> p.canParse(args)).findFirst();
    if (!isHelp && parser.isPresent()) {
      LOG.debug("Parsing using command using {}", parser.get());
      return parser.get().parse(args);
    }

    if (isHelp && parser.isPresent()) {
      LOG.debug("Building help display command using {}", parser.get());
      return () -> parser.get().displayHelp(executableName);
    }

    LOG.debug("Building global help command");
    return new GlobalHelpCommand(
        executableName,
        availableParsers
            .stream()
            .map(VetAdvancedCommandArgParser::getCommandArgName)
            .collect(Collectors.toList()),
        debugOptions);
  }

  /**
   * Created on 14/02/18.
   *
   * @author Reda.Housni-Alaoui
   */
  private static class GlobalHelpCommand implements VetCommand {

    private final String executableName;
    private final List<String> availableCommands;
    private final DebugOptions debugOptions;

    /**
     * @param executableName The name of the current executable
     * @param availableCommandNames The available command names
     */
    GlobalHelpCommand(
        String executableName, List<String> availableCommandNames, DebugOptions debugOptions) {
      if (StringUtils.isBlank(executableName)) {
        throw new IllegalArgumentException("appName can't be blank");
      }
      requireNonNull(availableCommandNames);
      requireNonNull(debugOptions);
      this.executableName = executableName;
      this.availableCommands = availableCommandNames;
      this.debugOptions = debugOptions;
    }

    @Override
    public void execute() {
      LOG.debug("Displaying global help");
      System.out.println(
          "usage: "
              + executableName
              + " <command>\n\n"
              + "where <command> is one of:\n"
              + " "
              + availableCommands.stream().distinct().collect(Collectors.joining(", "))
              + "\n\n"
              + executableName
              + " <command> "
              + StringUtils.join(HELP_ARGS, ",")
              + "  Display help on <command>"
              + "\n\n"
              + debugOptions.buildHelp());
    }
  }
}
