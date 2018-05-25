package com.cosium.vet.command;

import com.cosium.vet.VetVersion;
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
  private static final String HELP_OPT = "--help";
  private static final String VERSION_OPT = "--version";

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
    LOG.debug("Parsing arguments {}", Arrays.asList(args));

    if (Arrays.stream(args).anyMatch(VERSION_OPT::equals)) {
      return () -> {
        System.out.println(VetVersion.getValue());
        return null;
      };
    }

    boolean isHelp = Arrays.stream(args).anyMatch(HELP_OPT::equals);

    Optional<VetAdvancedCommandArgParser> parser =
        availableParsers.stream().filter(p -> p.canParse(args)).findFirst();
    if (!isHelp && parser.isPresent()) {
      LOG.debug("Parsing command using {}", parser.get());
      return parser.get().parse(args);
    }

    if (isHelp && parser.isPresent()) {
      LOG.debug("Building help display command using {}", parser.get());
      return () -> {
        parser.get().displayHelp(executableName);
        return null;
      };
    }

    LOG.debug("Building global help command");
    return new GlobalHelpCommand(
        executableName,
        availableParsers
            .stream()
            .filter(commandParser -> !VetHiddenCommandArgParser.class.isInstance(commandParser))
            .map(VetAdvancedCommandArgParser::getCommandArgName)
            .collect(Collectors.toList()),
        debugOptions);
  }

  /**
   * Created on 14/02/18.
   *
   * @author Reda.Housni-Alaoui
   */
  private static class GlobalHelpCommand implements VetCommand<Void> {

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
    public Void execute() {
      LOG.debug("Displaying global help");
      System.out.println(
          "usage: "
              + executableName
              + " ["
              + VERSION_OPT
              + "] ["
              + HELP_OPT
              + "]"
              + " <command> [<args>]\n\n"
              + "<command> can be one of:\n"
              + " "
              + availableCommands.stream().distinct().collect(Collectors.joining(", "))
              + "\n\n"
              + debugOptions.buildHelp()
              + "Vet: The Gerrit client using pull request review workflow");
      return null;
    }
  }
}
