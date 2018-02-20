package com.cosium.vet.runtime;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface UserInput {

  /**
   * @param question The question to ask
   * @param defaultValue The default value
   * @return The user answer
   */
  String ask(String question, String defaultValue);
}
