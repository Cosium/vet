package com.cosium.vet.git;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class GitConfigRepositoryTest {

  private GitConfigRepository tested;

  @Before
  public void before() throws Exception {
    GitTestRepository gitTestRepository = GitTestRepository.builder().build();
    GitProvider gitProvider = new GitProvider(gitTestRepository.repo, gitTestRepository.runner);
    tested = gitProvider.buildRepository();
  }

  @Test
  public void GIVEN_key_remote_to_origin_WHEN_getting_remote_THEN_it_should_return_origin() {
    assertThat(tested.getCurrentBranchValue("remote")).isEqualTo("origin");
  }

  @Test
  public void GIVEN_absent_key_foo_WHEN_getting_foo_THEN_it_should_return_null() {
    assertThat(tested.getCurrentBranchValue("foo")).isNull();
  }

  @Test
  public void GIVEN_set_key_foo_to_bar_WHEN_getting_foo_THEN_it_should_return_bar() {
    tested.setCurrentBranchValue("foo", "bar");
    assertThat(tested.getCurrentBranchValue("foo")).isEqualTo("bar");
  }

  @Test
  public void GIVEN_inexisting_key_foo_WHEN_setting_foo_to_null_THEN_it_should_not_fail() {
    tested.setCurrentBranchValue("foo", null);
  }
}
