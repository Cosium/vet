package com.cosium.vet.runtime;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface ProcessBuilderFactory {

  /**
   * @param command The process command
   * @return A new process builder
   */
  ProcessBuilder create(String... command);
}
