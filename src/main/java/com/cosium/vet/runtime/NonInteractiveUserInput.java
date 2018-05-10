package com.cosium.vet.runtime;

import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class NonInteractiveUserInput implements UserInput {

  private static final Logger LOG = LoggerFactory.getLogger(NonInteractiveUserInput.class);

  @Override
  public boolean askYesNo(String question, boolean defaultAnswer) {
    LOG.debug("Non interactive mode. Returning '{}'.", defaultAnswer);
    return defaultAnswer;
  }

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

  @Override
  public long askLong(String question) {
    throw new RuntimeException(
        String.format("Non interactive mode. Unable to answer to '%s'", question));
  }

  @Override
  public String ask(String question) {
    LOG.debug("Non interactive mode. Returning null.");
    return null;
  }
}
