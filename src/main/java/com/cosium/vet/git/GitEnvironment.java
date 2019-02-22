package com.cosium.vet.git;

import com.cosium.vet.runtime.Environment;

import java.util.Collections;
import java.util.Map;

/** @author Réda Housni Alaoui */
public class GitEnvironment implements Environment {

  private final Map<String, String> variableToValueMap;

  GitEnvironment(boolean interactive) {
    if (interactive) {
      variableToValueMap = Collections.emptyMap();
    } else {
      variableToValueMap = Collections.singletonMap("GIT_TERMINAL_PROMPT", String.valueOf(0));
    }
  }

  @Override
  public Map<String, String> asMap() {
    return variableToValueMap;
  }
}
