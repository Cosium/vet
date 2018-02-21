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
  public String askNonBlank(String question, String defaultValue) {
    LOG.debug("Non interactive mode. Returning '{}'.", defaultValue);
    return defaultValue;
  }

  @Override
  public String askNonBlank(String question) {
    throw new RuntimeException(
        String.format("Non interactive mode. Unable to answer to '%s'", question));
  }
}
