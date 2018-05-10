package com.cosium.vet.gerrit;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created on 10/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class CodeReviewVoteTest {

  @Test
  public void testPlus2() {
    CodeReviewVote.of("+2");
  }

  @Test
  public void test2() {
    assertThatThrownBy(() -> CodeReviewVote.of("2")).isInstanceOf(IllegalArgumentException.class);
  }
}
