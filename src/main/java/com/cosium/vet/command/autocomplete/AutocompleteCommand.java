package com.cosium.vet.command.autocomplete;

import com.cosium.vet.command.VetAdvancedCommandArgParser;
import com.cosium.vet.command.VetCommand;
import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;
import com.cosium.vet.runtime.UserOutput;
import com.cosium.vet.thirdparty.apache_commons_lang3.ArrayUtils;
import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Created on 11/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class AutocompleteCommand implements VetCommand<Void> {

  private static final Logger LOG = LoggerFactory.getLogger(AutocompleteCommand.class);

  private final UserOutput userOutput;
  private final List<VetAdvancedCommandArgParser> parsers;
  private final List<String> typedWordList;
  private final String currentWord;

  private AutocompleteCommand(
      UserOutput userOutput,
      List<VetAdvancedCommandArgParser> parsers,
      String[] typedWordList,
      Integer highlightedWordIndex) {
    this.userOutput = requireNonNull(userOutput);
    this.parsers = requireNonNull(parsers);

    while (highlightedWordIndex >= typedWordList.length) {
      typedWordList = ArrayUtils.add(typedWordList, StringUtils.EMPTY);
    }

    this.typedWordList = Arrays.asList(typedWordList);
    this.currentWord = this.typedWordList.get(highlightedWordIndex);
  }

  @Override
  public Void execute() {
    LOG.debug("Typed word array is {}", typedWordList);
    LOG.debug("Current word is '{}'", currentWord);

    if (typedWordList.isEmpty()) {
      LOG.debug("There should be at least the command name");
      return null;
    }

    if (typedWordList.size() == 1) {
      LOG.debug("No command entered. Printing all commands.");
      String possibilities =
          parsers
              .stream()
              .map(VetAdvancedCommandArgParser::getCommandArgName)
              .collect(Collectors.joining(StringUtils.LF));
      userOutput.display(possibilities, true);
      return null;
    }

    String firstWord = typedWordList.get(1);
    LOG.debug("First word is '{}'", firstWord);
    if (typedWordList.size() == 2) {
      LOG.debug("Only one typed word. Printing matching commands.");
      String possibilities =
          parsers
              .stream()
              .map(VetAdvancedCommandArgParser::getCommandArgName)
              .filter(commandName -> commandName.startsWith(firstWord))
              .collect(Collectors.joining(StringUtils.LF));
      userOutput.display(possibilities, true);
      return null;
    }

    VetAdvancedCommandArgParser parser =
        parsers
            .stream()
            .filter(p -> firstWord.equals(p.getCommandArgName()))
            .findFirst()
            .orElse(null);
    if (parser == null) {
      LOG.debug("No parser found for command '{}'", firstWord);
      return null;
    }

    String possibilities =
        parser
            .getMatchingOptions(currentWord)
            .stream()
            .filter(opt -> !typedWordList.contains(opt))
            .collect(Collectors.joining(StringUtils.LF));
    userOutput.display(possibilities);
    return null;
  }

  public static class Factory implements AutocompleteCommandFactory {

    private final UserOutput userOutput;
    private final List<VetAdvancedCommandArgParser> parsers;

    public Factory(UserOutput userOutput, List<VetAdvancedCommandArgParser> parsers) {
      this.userOutput = requireNonNull(userOutput);
      this.parsers = requireNonNull(parsers);
    }

    @Override
    public AutocompleteCommand build(String[] typedWordArray, Integer highlightedWordIndex) {
      return new AutocompleteCommand(userOutput, parsers, typedWordArray, highlightedWordIndex);
    }
  }
}
