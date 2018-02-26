package com.cosium.vet;

/**
 * Created on 14/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface VetCommandArgParser {

  /** Display help for the managed command */
  void displayHelp(String executableName);

  /** @return The command arg name. i.e. 'push' */
  String getCommandArgName();

  /**
   * @param args The arguments to parse
   * @return True if the current parser is able to parse the arguments
   */
  boolean canParse(String... args);

  /**
   * Parse the arguments and return a new command.
   *
   * @param args The arguments to parse
   * @return The parsed command
   */
  VetCommand parse(String... args);
}
