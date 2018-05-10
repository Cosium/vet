package com.cosium.vet.runtime;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface UserInput {

  /**
   * @param question The question to ask
   * @param defaultAnswer The default value
   * @return True if the user answered yes
   */
  boolean askYesNo(String question, boolean defaultAnswer);

  /**
   * @param question The question to ask
   * @param defaultValue The default value
   * @return The user answer that cannot be blank
   */
  String askNonBlank(String question, String defaultValue);

  /**
   * @param question The question to ask
   * @return The user answer that cannot be blank
   */
  String askNonBlank(String question);

  /**
   * @param question The question to ask
   * @return The user answer that must be a long
   */
  long askLong(String question);

  /**
   * @param question The question to ask
   * @return The user answer that can be blank
   */
  String ask(String question);
}
