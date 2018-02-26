package com.cosium.vet.log;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;

/**
 * Gradle with Java 9 Module conflicts with SLF4J compile dependency.<br>
 * This custom logging system should be replaced by SLF4J as soon as
 * https://github.com/gradle/gradle/issues/2657#issuecomment-368320629 is fixed.
 *
 * <p>Created on 25/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class LoggerFactory {

  private static final AtomicReference<Level> level = new AtomicReference<>(Level.INFO);
  private static final AtomicBoolean printStackTrace = new AtomicBoolean(true);
  private static final AtomicBoolean printContext = new AtomicBoolean(true);

  public static Logger getLogger(Class<?> clazz) {
    return new DefaultLogger(clazz);
  }

  static Level getLevel() {
    return level.get();
  }

  public static void setLevel(Level level) {
    requireNonNull(level);
    LoggerFactory.level.set(level);
  }

  static boolean isPrintStackTrace() {
    return printStackTrace.get();
  }

  public static void setPrintStackTrace(boolean printStackTrace) {
    LoggerFactory.printStackTrace.set(printStackTrace);
  }

  static boolean isPrintContext() {
    return printContext.get();
  }

  public static void setPrintContext(boolean printContext) {
    LoggerFactory.printContext.set(printContext);
  }
}
