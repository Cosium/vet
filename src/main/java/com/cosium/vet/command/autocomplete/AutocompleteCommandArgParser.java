package com.cosium.vet.command.autocomplete;

import com.cosium.vet.command.AbstractVetAdvancedCommandArgParser;
import com.cosium.vet.command.VetCommand;
import com.cosium.vet.command.VetHiddenCommandArgParser;
import com.cosium.vet.thirdparty.apache_commons_cli.*;
import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * Created on 11/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class AutocompleteCommandArgParser extends AbstractVetAdvancedCommandArgParser
    implements VetHiddenCommandArgParser {

  private static final String COMMAND_NAME = "autocomplete";

  private static final String TYPED_WORD_ARRAY = "a";
  private static final String HIGHLIGHTED_WORD_INDEX = "i";

  private final AutocompleteCommandFactory factory;

  public AutocompleteCommandArgParser(AutocompleteCommandFactory factory) {
    super(
        new Options()
            .addOption(
                Option.builder(TYPED_WORD_ARRAY)
                    .argName("array")
                    .longOpt("typed-word-array")
                    .hasArg()
                    .desc("The array of typed words. Space separated words.")
                    .build())
            .addOption(
                Option.builder(HIGHLIGHTED_WORD_INDEX)
                    .argName("index")
                    .longOpt("highlighted-word-index")
                    .hasArg()
                    .desc("The index of the highlighted word.")
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
        "Autocompletes command",
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

    String[] typedWordArray =
        ofNullable(commandLine.getOptionValue(TYPED_WORD_ARRAY))
            .map(s -> StringUtils.split(s, StringUtils.SPACE))
            .orElse(new String[0]);

    Integer highlightedWordIndex =
        ofNullable(commandLine.getOptionValue(HIGHLIGHTED_WORD_INDEX))
            .map(Integer::parseInt)
            .orElse(null);

    return factory.build(typedWordArray, highlightedWordIndex);
  }
}
