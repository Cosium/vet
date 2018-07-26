package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchRefName;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

  @Test
  public void testBranchRefName() {
    ChangeNumericId numericId = ChangeNumericId.of(1234);
    Patchset patch = mock(Patchset.class);
    when(patch.getNumber()).thenReturn(5);

    assertThat(numericId.branchRefName(patch))
        .isEqualTo(BranchRefName.of("refs/changes/34/1234/5"));
  }
}
