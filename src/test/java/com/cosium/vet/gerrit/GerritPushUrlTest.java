package com.cosium.vet.gerrit;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class GerritPushUrlTest {

  private static final String PROJECT_NAME = "baz";
  private static final String ROOT_URL = "https://foo.com/bar";

  private static final String PUSH_URL = ROOT_URL + "/" + PROJECT_NAME;
  private static final String PUSH_URL_WITH_SLASH = PUSH_URL + "/";

  @Test
  public void testParseProjectName() {
    assertThat(GerritPushUrl.of(PUSH_URL).parseProjectName().value()).isEqualTo(PROJECT_NAME);
    assertThat(GerritPushUrl.of(PUSH_URL_WITH_SLASH).parseProjectName().value()).isEqualTo(PROJECT_NAME);
  }

  @Test
  public void testParseRootUrl() {
    assertThat(GerritPushUrl.of(PUSH_URL).parseHttpRootUrl().toString()).isEqualTo(ROOT_URL);
    assertThat(GerritPushUrl.of(PUSH_URL_WITH_SLASH).parseHttpRootUrl().toString()).isEqualTo(ROOT_URL);
  }
}
