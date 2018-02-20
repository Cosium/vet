package com.cosium.vet.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class NonInteractiveUserInput implements UserInput {

  private static final Logger LOG = LoggerFactory.getLogger(NonInteractiveUserInput.class);

  @Override
  public String ask(String question, String defaultValue) {
    LOG.debug("Non interactive mode. Returning default value.");
    return defaultValue;
  }
}
