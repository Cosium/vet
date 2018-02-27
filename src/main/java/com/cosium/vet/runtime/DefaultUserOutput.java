package com.cosium.vet.runtime;

/**
 * Created on 23/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class DefaultUserOutput implements UserOutput {
  @Override
  public void display(String message) {
    System.out.print(message);
  }
}
