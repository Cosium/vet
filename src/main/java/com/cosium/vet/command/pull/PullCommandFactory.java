package com.cosium.vet.command.pull;

/**
 * Created on 09/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface PullCommandFactory {

  /** @return A new track command */
  PullCommand build();
}
