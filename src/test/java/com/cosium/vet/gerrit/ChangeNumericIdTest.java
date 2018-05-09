package com.cosium.vet.gerrit;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 09/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class ChangeNumericIdTest {

  @Test
  public void testParseFromPushToRefOutput() {
    ChangeNumericId numericId =
        ChangeNumericId.parseFromPushToRefForOutput(
            PushUrl.of("https://gerrit-test.cosium.com/agent"),
            "remote: Processing changes: new: 1, done    \n"
                + "remote: \n"
                + "remote: New Changes:\n"
                + "remote:   https://gerrit-test.cosium.com/#/c/agent/+/1451 hehe Reviewed-on: https://gerrit-test.cosium.com/1448 Reviewed-by: tester ...\n"
                + "remote:");
    assertThat(numericId).isEqualTo(ChangeNumericId.of(1451));
  }
}
