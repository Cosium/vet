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

  private static final String NO_PROTOCOL_ROOT_URL = "foo.com/bar";
  private static final String NO_PROCOCOL_PUSH_URL = NO_PROTOCOL_ROOT_URL + "/" + PROJECT_NAME;
  private static final String NO_PROTOCOL_PUSH_URL_WITH_SLASH = NO_PROCOCOL_PUSH_URL + "/";

  @Test
  public void testParseHttpProjectName() {
    testParseProjectName(PROJECT_NAME, PUSH_URL, PUSH_URL_WITH_SLASH);
    testParseProjectName(PROJECT_NAME, NO_PROCOCOL_PUSH_URL, NO_PROTOCOL_PUSH_URL_WITH_SLASH);
  }

  private void testParseProjectName(String projectName, String withoutSlash, String withSlash) {
    assertThat(GerritPushUrl.of(withoutSlash).parseProjectName().toString()).isEqualTo(projectName);
    assertThat(GerritPushUrl.of(withSlash).parseProjectName().toString()).isEqualTo(projectName);
  }
}
