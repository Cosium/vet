package com.cosium.vet.command.status;

/**
 * Created on 09/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface StatusCommandFactory {

  /** @return A new command */
  StatusCommand build();
}
