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

  private static final String HTTPS_PUSH_URL = "https://foo.com/biz/baz";
  private static final String HTTPS_PUSH_URL_WITH_SLASH = HTTPS_PUSH_URL + "/";

  private static final String SSH_PUSH_URL = "https://foo.com:29418/biz/baz";
  private static final String SSH_PUSH_URL_WITH_SLASH = SSH_PUSH_URL + "/";

  @Test
  public void testParseHttpsProjectName() {
    assertThat(PushUrl.of(HTTPS_PUSH_URL).parseProjectName().toString()).isEqualTo(PROJECT_NAME);
    assertThat(PushUrl.of(HTTPS_PUSH_URL_WITH_SLASH).parseProjectName().toString())
        .isEqualTo(PROJECT_NAME);
  }

  @Test
  public void testParseSshProjectName() {
    assertThat(PushUrl.of(SSH_PUSH_URL).parseProjectName().toString()).isEqualTo(PROJECT_NAME);
    assertThat(PushUrl.of(SSH_PUSH_URL_WITH_SLASH).parseProjectName().toString())
        .isEqualTo(PROJECT_NAME);
  }

  @Test
  public void testComputeChangeWebUrlFromHttps() {
    String changeUrl = PushUrl.of(HTTPS_PUSH_URL).computeChangeWebUrl(ChangeNumericId.of(1234));
    assertThat(changeUrl).isEqualTo("https://foo.com/c/biz/baz/+/1234");
  }

  @Test
  public void testComputeChangeWebUrlFromSsh() {
    String changeUrl = PushUrl.of(SSH_PUSH_URL).computeChangeWebUrl(ChangeNumericId.of(1234));
    assertThat(changeUrl).isEqualTo("https://foo.com/c/biz/baz/+/1234");
  }

  @Test
  public void testComputeChangeWebUrlWithSlashFromHttps() {
    String changeUrl =
        PushUrl.of(HTTPS_PUSH_URL_WITH_SLASH).computeChangeWebUrl(ChangeNumericId.of(1234));
    assertThat(changeUrl).isEqualTo("https://foo.com/c/biz/baz/+/1234");
  }

  @Test
  public void testComputeChangeWebUrlWithSlashFromSsh() {
    String changeUrl =
        PushUrl.of(SSH_PUSH_URL_WITH_SLASH).computeChangeWebUrl(ChangeNumericId.of(1234));
    assertThat(changeUrl).isEqualTo("https://foo.com/c/biz/baz/+/1234");
  }
}
