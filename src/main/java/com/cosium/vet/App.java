package com.cosium.vet;

import com.cosium.vet.command.DebugOptions;
import com.cosium.vet.log.Level;
import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class App {

  private static final Logger LOG = LoggerFactory.getLogger(App.class);

  private static final List<String> STACKTRACE_ARG_NAMES = List.of("-x", "--stackstrace");
  private static final List<String> VERBOSE_ARG_NAMES = List.of("-v", "--verbose");

  public static void main(String[] args) {
    boolean stacktrace = Arrays.stream(args).anyMatch(STACKTRACE_ARG_NAMES::contains);
    boolean verbose = Arrays.stream(args).anyMatch(VERBOSE_ARG_NAMES::contains);
    LoggerFactory.setPrintStackTrace(stacktrace);
    LoggerFactory.setPrintContext(verbose);
    if (verbose) {
      LoggerFactory.setLevel(Level.TRACE);
    }
    try {
      new Vet(true, new DebugOptions(STACKTRACE_ARG_NAMES, VERBOSE_ARG_NAMES)).run(args);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      System.exit(1);
    }
  }
}
