package com.cosium.vet.gerrit;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class PushUrlTest {

  private static final String PROJECT_NAME = "biz/baz";

  private static final String PUSH_URL = "https://foo.com/biz/baz";
  private static final String PUSH_URL_WITH_SLASH = PUSH_URL + "/";

  @Test
  public void testParseHttpProjectName() {
    assertThat(PushUrl.of(PUSH_URL).parseProjectName().toString()).isEqualTo(PROJECT_NAME);
    assertThat(PushUrl.of(PUSH_URL_WITH_SLASH).parseProjectName().toString())
        .isEqualTo(PROJECT_NAME);
  }

  @Test
  public void testComputeChangeUrl() {
    String changeUrl = PushUrl.of(PUSH_URL).computeChangeWebUrl(ChangeNumericId.of(1234));
    assertThat(changeUrl).isEqualTo("https://foo.com/c/biz/baz/+/1234");
  }

  @Test
  public void testComputeChangeUrlWithSlash() {
    String changeUrl =
        PushUrl.of(PUSH_URL_WITH_SLASH).computeChangeWebUrl(ChangeNumericId.of(1234));
    assertThat(changeUrl).isEqualTo("https://foo.com/c/biz/baz/+/1234");
  }
}
