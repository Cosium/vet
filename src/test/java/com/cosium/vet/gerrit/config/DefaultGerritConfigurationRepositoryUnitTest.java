package com.cosium.vet.gerrit.config;

import com.cosium.vet.gerrit.ChangeNumericId;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitConfigRepository;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultGerritConfigurationRepositoryUnitTest {
  private static final String VET_TRACKED_CHANGE_TARGET_BRANCH = "vet-tracked-change-target-branch";
  private static final String VET_TRACKED_CHANGE_NUMERIC_ID = "vet-tracked-change-numeric-id";

  private static final BranchShortName CHANGE_TARGET_BRANCH = BranchShortName.MASTER;

  private GitConfigRepository gitConfigRepository;
  private DefaultGerritConfigurationRepository tested;

  @Before
  public void before() {
    gitConfigRepository = mock(GitConfigRepository.class);
    tested = new DefaultGerritConfigurationRepository(gitConfigRepository);
  }

  @Test
  public void GIVEN_no_file_WHEN_read_THEN_it_should_return_empty_conf() {
    GerritConfiguration gerritConfiguration = tested.read();
    assertThat(gerritConfiguration).isNotNull();
  }

  @Test
  public void GIVEN_conf_containing_targetbranch_master_WHEN_read_THEN_it_should_return_master() {
    when(gitConfigRepository.getCurrentBranchValue(VET_TRACKED_CHANGE_TARGET_BRANCH))
        .thenReturn(CHANGE_TARGET_BRANCH.toString());

    GerritConfiguration gerritConfiguration = tested.read();
    assertThat(gerritConfiguration).isNotNull();
    assertThat(gerritConfiguration.getTrackedChangeTargetBranch()).contains(CHANGE_TARGET_BRANCH);
  }

  @Test
  public void
      GIVEN_conf_containing_tracked_change_numeric_id_1234_WHEN_read_THEN_it_should_return_1234() {
    when(gitConfigRepository.getCurrentBranchValue(VET_TRACKED_CHANGE_NUMERIC_ID))
        .thenReturn("1234");

    GerritConfiguration gerritConfiguration = tested.read();
    assertThat(gerritConfiguration).isNotNull();
    assertThat(gerritConfiguration.getTrackedChangeNumericId()).contains(ChangeNumericId.of(1234));
  }

  @Test
  public void
      GIVEN_conf_containing_non_numeric_tracked_change_id_foo_WHEN_read_THEN_it_should_return_empty() {
    when(gitConfigRepository.getCurrentBranchValue(VET_TRACKED_CHANGE_NUMERIC_ID))
        .thenReturn("foo");

    GerritConfiguration gerritConfiguration = tested.read();
    assertThat(gerritConfiguration).isNotNull();
    assertThat(gerritConfiguration.getTrackedChangeNumericId()).isEmpty();
  }

  @Test
  public void WHEN_writing_tracked_change_numeric_id_THEN_it_writes_it_to_git_config_repo() {
    tested.readAndWrite(
        gerritConfiguration -> {
          gerritConfiguration.setTrackedChangeNumericId(ChangeNumericId.of(1234));
          return gerritConfiguration;
        });

    verify(gitConfigRepository).setCurrentBranchValue(VET_TRACKED_CHANGE_NUMERIC_ID, "1234");
  }

  @Test
  public void WHEN_writing_tracked_change_target_branch_THEN_it_writes_it_to_git_config_repo() {
    tested.readAndWrite(
        gerritConfiguration -> {
          gerritConfiguration.setTrackedChangeTargetBranch(CHANGE_TARGET_BRANCH);
          return gerritConfiguration;
        });

    verify(gitConfigRepository)
        .setCurrentBranchValue(VET_TRACKED_CHANGE_TARGET_BRANCH, CHANGE_TARGET_BRANCH.toString());
  }
}
