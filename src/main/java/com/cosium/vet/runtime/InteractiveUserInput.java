package com.cosium.vet.runtime;

import org.apache.commons.lang3.StringUtils;

import java.util.Scanner;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class InteractiveUserInput implements UserInput {
  @Override
  public String ask(String question, String defaultValue) {
    Scanner scanner = new Scanner(System.in);
    System.out.println(String.format("%s [%s]:", question, defaultValue));
    String value = null;
    while (StringUtils.isBlank(value)) {
      value = StringUtils.defaultIfBlank(scanner.nextLine(), defaultValue);
    }
    return value;
  }
}
