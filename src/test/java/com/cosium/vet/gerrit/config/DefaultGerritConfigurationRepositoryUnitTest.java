package com.cosium.vet.gerrit.config;

import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitConfigRepository;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultGerritConfigurationRepositoryUnitTest {

  private static final BranchShortName CHANGE_TARGET_BRANCH = BranchShortName.MASTER;

  private GitConfigRepository gitConfigProvider;
  private DefaultGerritConfigurationRepository tested;

  @Before
  public void before() {
    gitConfigProvider = mock(GitConfigRepository.class);
    tested = new DefaultGerritConfigurationRepository(gitConfigProvider);
  }

  @Test
  public void GIVEN_no_file_WHEN_read_THEN_it_should_return_empty_conf() {
    GerritConfiguration gerritConfiguration = tested.read();
    assertThat(gerritConfiguration).isNotNull();
  }

  @Test
  public void GIVEN_conf_containing_targetbranch_master_WHEN_read_THEN_it_should_return_master() {
    when(gitConfigProvider.getCurrentBranchValue("vet-change-target-branch"))
        .thenReturn(CHANGE_TARGET_BRANCH.value());

    GerritConfiguration gerritConfiguration = tested.read();
    assertThat(gerritConfiguration).isNotNull();
    assertThat(gerritConfiguration.getChangeTargetBranch()).contains(CHANGE_TARGET_BRANCH);
  }
}
