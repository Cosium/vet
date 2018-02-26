package com.cosium.vet.log;

/**
 * Created on 25/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface Logger {

  void error(String message, Object... parameters);

  void warn(String message, Object... parameters);

  void info(String message, Object... parameters);

  void debug(String message, Object... parameters);

  void trace(String message, Object... parameters);
}
