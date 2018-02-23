package com.cosium.vet.runtime;

import org.apache.commons.lang3.StringUtils;

import static java.util.Objects.requireNonNull;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class InteractiveUserInput implements UserInput {

  private final InputScanner inputScanner;
  private final UserOutput userOutput;

  public InteractiveUserInput() {
    this(new DefaultInputScanner(), new DefaultUserOutput());
  }

  InteractiveUserInput(InputScanner inputScanner, UserOutput userOutput) {
    requireNonNull(inputScanner);
    requireNonNull(userOutput);
    this.inputScanner = inputScanner;
    this.userOutput = userOutput;
  }

  @Override
  public String askNonBlank(String question, String defaultValue) {
    String value = null;
    while (StringUtils.isBlank(value)) {
      userOutput.display(String.format("%s [%s]:", question, defaultValue));
      value = StringUtils.defaultIfBlank(inputScanner.nextLine(), defaultValue);
    }
    return value;
  }

  @Override
  public String askNonBlank(String question) {
    String value = null;
    while (StringUtils.isBlank(value)) {
      userOutput.display(String.format("%s:", question));
      value = inputScanner.nextLine();
    }
    return value;
  }

  @Override
  public String ask(String question) {
    userOutput.display(String.format("%s:", question));
    return inputScanner.nextLine();
  }
}
