package com.cosium.vet.command;

/**
 * Created on 14/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
@FunctionalInterface
public interface VetCommand<T> {

  /** Executes the command */
  T execute();
}
