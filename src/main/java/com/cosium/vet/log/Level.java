package com.cosium.vet.log;

/**
 * Created on 25/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
enum Level {
  ERROR(4),
  WARN(3),
  INFO(2),
  DEBUG(1),
  TRACE(0);

  private final int precedence;

  Level(int precedence) {
    this.precedence = precedence;
  }

  public int getPrecedence() {
    return precedence;
  }
}
