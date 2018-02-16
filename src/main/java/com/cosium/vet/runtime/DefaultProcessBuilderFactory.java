package com.cosium.vet.runtime;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultProcessBuilderFactory implements ProcessBuilderFactory {
  @Override
  public ProcessBuilder create(String... command) {
    return new ProcessBuilder(command);
  }
}
