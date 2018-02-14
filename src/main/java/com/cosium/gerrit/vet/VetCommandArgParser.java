package com.cosium.gerrit.vet;

import java.util.Optional;

/**
 * Created on 14/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
@FunctionalInterface
public interface VetCommandArgParser {

  /**
   * Parse the arguments and maybe return a new command.
   *
   * @param args The arguments to parse
   * @return The parsed command or empty
   */
  Optional<VetCommand> parse(String args[]);
}
