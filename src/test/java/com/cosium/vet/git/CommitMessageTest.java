package com.cosium.vet.git;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 27/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class CommitMessageTest {

  @Test
  public void testremoveLinesContaining() {
    String message =
        CommitMessage.of(
                "Merge remote-tracking branch 'origin/master' into feature/postgres-unsafe\n"
                    + "\n"
                    + "Change-Id: Iac23231451e6dc7736f69a7b4e7e3cc8a1228241")
            .removeLinesStartingWith("Change-Id: ");

    assertThat(message)
        .isEqualTo("Merge remote-tracking branch 'origin/master' into feature/postgres-unsafe");
  }
}
