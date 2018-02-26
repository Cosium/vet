package com.cosium.vet.command;

/**
 * Created on 26/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface VetCommandArgParser {
  /**
   * Parse the arguments and return a new command.
   *
   * @param args The arguments to parse
   * @return The parsed command
   */
  VetCommand parse(String... args);
}
