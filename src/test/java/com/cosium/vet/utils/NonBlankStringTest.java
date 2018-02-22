package com.cosium.vet.utils;

import com.cosium.vet.utils.NonBlankString;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class NonBlankStringTest {

  @Test
  public void testBlank() {
    assertThatThrownBy(() -> new Stub("   ")).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void testNonBlank() {
    assertThat(new Stub("foo").toString()).isEqualTo("foo");
  }

  private class Stub extends NonBlankString {
    Stub(String value) {
      super(value);
    }
  }
}
