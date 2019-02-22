package com.cosium.vet.runtime;

import java.util.Collections;

/** @author Réda Housni Alaoui */
public class Environments {

  private static final Environment EMPTY = Collections::emptyMap;

  private Environments() {}

  public static Environment empty() {
    return EMPTY;
  }
}
