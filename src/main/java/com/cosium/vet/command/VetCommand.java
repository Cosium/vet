package com.cosium.vet.command;

/**
 * Created on 14/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
@FunctionalInterface
public interface VetCommand {

  /** Executes the command */
  void execute();
}
