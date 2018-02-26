package com.cosium.vet.command;

import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Created on 26/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DebugOptions {

  private final List<String> stracktraceEnabledArgNames;
  private final List<String> verboseArgNames;
  private final boolean empty;

  public DebugOptions(List<String> stracktraceEnabledArgNames, List<String> verboseArgNames) {
    requireNonNull(stracktraceEnabledArgNames);
    requireNonNull(verboseArgNames);
    this.stracktraceEnabledArgNames = stracktraceEnabledArgNames;
    this.verboseArgNames = verboseArgNames;
    this.empty = stracktraceEnabledArgNames.isEmpty() && verboseArgNames.isEmpty();
  }

  public static DebugOptions empty() {
    return new DebugOptions(Collections.emptyList(), Collections.emptyList());
  }

  public String buildHelp() {
    if (empty) {
      return StringUtils.EMPTY;
    }

    StringBuilder builder = new StringBuilder();
    builder.append("Debug options:").append("\n");
    if (!stracktraceEnabledArgNames.isEmpty()) {
      builder
          .append(" ")
          .append(StringUtils.join(stracktraceEnabledArgNames, ","))
          .append("     Enable stacktrace display")
          .append("\n");
    }
    if (!verboseArgNames.isEmpty()) {
      builder
          .append(" ")
          .append(StringUtils.join(verboseArgNames, ","))
          .append("         Enable verbose mode")
          .append("\n");
    }
    return builder.toString();
  }
}
