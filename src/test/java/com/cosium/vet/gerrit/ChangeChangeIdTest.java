package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchShortName;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 23/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class ChangeChangeIdTest {

  @Test
  public void test() {
    String value =
        ChangeChangeId.builder()
            .project(GerritProjectName.of("foo"))
            .sourceBranch(BranchShortName.of("feature"))
            .targetBranch(BranchShortName.of("target"))
            .build()
            .toString();

    // The expected result must remain constant
    assertThat(value).isEqualTo("I47d5e4224d7cb6fe731541a4e9451fbb03e6effe");
  }
}
