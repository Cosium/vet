package com.cosium.vet.runtime;

import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;
import com.cosium.vet.thirdparty.apache_commons_lang3.math.NumberUtils;

import static java.util.Objects.requireNonNull;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class InteractiveUserInput implements UserInput {

  private final InputScanner inputScanner;
  private final UserOutput userOutput;

  public InteractiveUserInput(UserOutput userOutput) {
    this(new DefaultInputScanner(), userOutput);
  }

  InteractiveUserInput(InputScanner inputScanner, UserOutput userOutput) {
    requireNonNull(inputScanner);
    requireNonNull(userOutput);
    this.inputScanner = inputScanner;
    this.userOutput = userOutput;
  }

  @Override
  public boolean askYesNo(String question, boolean defaultAnswer) {
    String value = null;
    String messageToDisplay =
        String.format(
            "%s [%s/%s]:", question, defaultAnswer ? "Y" : "y", defaultAnswer ? "n" : "N");
    while (StringUtils.isBlank(value)) {
      userOutput.display(messageToDisplay);
      value = StringUtils.defaultIfBlank(inputScanner.nextLine(), defaultAnswer ? "y" : "n");
    }
    return "y".equalsIgnoreCase(value);
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
  public long askLong(String question) {
    String value = null;
    while (StringUtils.isBlank(value) || !NumberUtils.isDigits(value)) {
      userOutput.display(String.format("%s:", question));
      value = inputScanner.nextLine();
    }
    return Long.parseLong(value);
  }

  @Override
  public String ask(String question) {
    userOutput.display(String.format("%s:", question));
    return inputScanner.nextLine();
  }
}
