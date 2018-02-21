package com.cosium.vet.gerrit;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class GerritHttpRootUrlTest {

  private static final String SSH_URL = "ssh://foo.com";
  private static final String HTTP_URL = "http://foo.com";
  private static final String HTTPS_URL = "https://foo.com";

  @Test
  public void testNonHttpURL() {
    assertThatThrownBy(() -> GerritHttpRootUrl.of(SSH_URL))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void testHttpURL() {
    assertThat(GerritHttpRootUrl.of(HTTP_URL).toString()).isEqualTo(HTTP_URL);
  }

  @Test
  public void testHttpsURL() {
    assertThat(GerritHttpRootUrl.of(HTTPS_URL).toString()).isEqualTo(HTTPS_URL);
  }

}
