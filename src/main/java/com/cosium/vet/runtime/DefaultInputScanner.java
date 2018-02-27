package com.cosium.vet.runtime;

import java.util.Scanner;

/**
 * Created on 23/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class DefaultInputScanner implements InputScanner {

  private final Scanner scanner = new Scanner(System.in);

  @Override
  public String nextLine() {
    String line = scanner.nextLine();
    System.out.println();
    return line;
  }
}
