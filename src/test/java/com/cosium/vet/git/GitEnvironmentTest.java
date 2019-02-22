package com.cosium.vet.git;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** @author Réda Housni Alaoui */
public class GitEnvironmentTest {

  @Test
  public void GIT_TERMINAL_PROMPT_is_disabled_in_non_interactive_mode() {
    assertThat(new GitEnvironment(false).asMap())
        .hasSize(1)
        .containsEntry("GIT_TERMINAL_PROMPT", "0");
  }

  @Test
  public void is_empty_in_interactive_mode() {
    assertThat(new GitEnvironment(true).asMap()).isEmpty();
  }
}
