package com.cosium.vet.log;

import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/**
 * Created on 25/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class DefaultLogger implements Logger {

  private static final Pattern MUSTACHE = Pattern.compile("\\{}");
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("kk:mm:ss");

  private final Class<?> clazz;

  DefaultLogger(Class<?> clazz) {
    requireNonNull(clazz);
    this.clazz = clazz;
  }

  @Override
  public void error(String message, Object... parameters) {
    doLog(Level.ERROR, message, parameters);
  }

  @Override
  public void warn(String message, Object... parameters) {
    doLog(Level.WARN, message, parameters);
  }

  @Override
  public void info(String message, Object... parameters) {
    doLog(Level.INFO, message, parameters);
  }

  @Override
  public void debug(String message, Object... parameters) {
    doLog(Level.DEBUG, message, parameters);
  }

  @Override
  public void trace(String message, Object... parameters) {
    doLog(Level.TRACE, message, parameters);
  }

  private void doLog(Level level, String message, Object... parameters) {
    if (level.getPrecedence() < LoggerFactory.getLevel().getPrecedence()) {
      return;
    }

    StringBuilder log = new StringBuilder();

    if (LoggerFactory.isPrintContext()) {
      log.append(TIME_FORMATTER.format(LocalDateTime.now()))
          .append(StringUtils.SPACE)
          .append(level)
          .append(StringUtils.SPACE)
          .append(clazz.getName())
          .append(StringUtils.SPACE)
          .append("- ");
    }

    Matcher matcher = MUSTACHE.matcher(StringUtils.defaultString(message));
    int consumedParamIndex = -1;
    while (matcher.find()) {
      consumedParamIndex++;
      matcher.appendReplacement(log, String.valueOf(parameters[consumedParamIndex]));
    }
    matcher.appendTail(log);

    System.out.println(log);

    if (!LoggerFactory.isPrintStackTrace()) {
      return;
    }
    if (consumedParamIndex == parameters.length - 1) {
      return;
    }

    Object throwable = parameters[parameters.length - 1];
    if (throwable instanceof Throwable) {
      ((Throwable) throwable).printStackTrace(System.out);
    }
  }
}
