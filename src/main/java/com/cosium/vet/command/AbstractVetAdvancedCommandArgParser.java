package com.cosium.vet.command;

import com.cosium.vet.thirdparty.apache_commons_cli.Options;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Created on 11/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public abstract class AbstractVetAdvancedCommandArgParser implements VetAdvancedCommandArgParser {

  private final Options options;

  public AbstractVetAdvancedCommandArgParser(Options options) {
    this.options = requireNonNull(options);
  }

  @Override
  public List<String> getMatchingOptions(String word) {
    return options
        .getMatchingOptions(word)
        .stream()
        .map(s -> "--" + s)
        .collect(Collectors.toList());
  }

  protected Options getOptions() {
    return options;
  }
}
