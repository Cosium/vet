package com.cosium.vet.runtime;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class NonInteractiveUserInputTest {

  private NonInteractiveUserInput nonInteractiveUserInput = new NonInteractiveUserInput();

  @Test
  public void testAsk() {
    assertThat(nonInteractiveUserInput.askNonBlank("Who are you", "foo")).isEqualTo("foo");
  }
}
