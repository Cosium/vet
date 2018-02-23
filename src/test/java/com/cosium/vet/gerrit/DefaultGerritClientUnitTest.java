package com.cosium.vet.gerrit;

import com.cosium.vet.gerrit.config.GerritConfiguration;
import com.cosium.vet.gerrit.config.GerritConfigurationRepository;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.GitUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Created on 23/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultGerritClientUnitTest {

  private static final String HELLO_WORLD = "Hello World";
  private static final String FOO = "foo";
  private static final String WHERE_IS_MY_MIND = "Where is my mind?!";

  private AtomicReference<GerritConfiguration> lastSavedConfiguration;

  private GerritConfiguration gerritConfiguration;
  private ChangeChangeId changeChangeId;
  private GitClient git;
  private GerritPushUrl pushUrl;

  private GerritClient tested;

  @Before
  public void before() {
    lastSavedConfiguration = new AtomicReference<>();

    GerritConfigurationRepository configurationRepository =
        mock(GerritConfigurationRepository.class);
    gerritConfiguration = mock(GerritConfiguration.class);
    when(configurationRepository.read()).thenReturn(gerritConfiguration);
    when(configurationRepository.readAndWrite(any()))
        .thenAnswer(
            invocation -> {
              Function<GerritConfiguration, ?> func = invocation.getArgument(0);
              Object res = func.apply(gerritConfiguration);
              lastSavedConfiguration.set(gerritConfiguration);
              return res;
            });
    ChangeChangeIdFactory changeChangeIdFactory = mock(ChangeChangeIdFactory.class);
    changeChangeId = mock(ChangeChangeId.class);
    when(changeChangeIdFactory.build(any(), any())).thenReturn(changeChangeId);

    git = mock(GitClient.class);
    when(git.getBranch()).thenReturn(BranchShortName.of("feature/a"));

    pushUrl = GerritPushUrl.of("https://bar.com/foo");

    tested = new DefaultGerritClient(configurationRepository, changeChangeIdFactory, git, pushUrl);
  }

  @Test
  public void GIVEN_no_current_change_WHEN_get_change_THEN_it_should_return_empty() {
    assertThat(tested.getChange()).isEmpty();
  }

  @Test
  public void
      GIVEN_current_change_targeting_feature_branch_WHEN_get_change_THEN_it_should_return_change_for_feature() {
    BranchShortName feature = BranchShortName.of("feature");
    when(gerritConfiguration.getChangeTargetBranch()).thenReturn(Optional.of(feature));

    tested
        .getChange()
        .ifPresentOrElse(
            change -> assertThat(change.getTargetBranch()).isEqualTo(feature), Assert::fail);
  }

  @Test
  public void WHEN_set_get_change_to_master_THEN_it_should_return_change_for_master() {
    assertThat(tested.setAndGetChange(BranchShortName.MASTER).getTargetBranch())
        .isEqualTo(BranchShortName.MASTER);
  }

  @Test
  public void WHEN_set_get_change_to_master_THEN_it_should_store_master_in_configuration() {
    tested.setAndGetChange(BranchShortName.MASTER);

    assertThat(lastSavedConfiguration.get()).isNotNull();
    verify(lastSavedConfiguration.get()).setChangeTargetBranch(BranchShortName.MASTER);
  }

  @Test
  public void
      GIVEN_last_commit_message_hello_world_WHEN_create_patch_set_THEN_commit_tree_message_should_contain_hello_world() {
    when(git.getLastCommitMessage()).thenReturn(HELLO_WORLD);
    GerritChange gerritChange = tested.setAndGetChange(BranchShortName.MASTER);

    tested.createPatchSet(gerritChange, "start", "end", null);

    verify(git).commitTree(any(), any(), contains(HELLO_WORLD));
  }

  @Test
  public void
      GIVEN_changeid_I1234_WHEN_create_patch_set_THEN_commit_tree_message_should_end_with_I1234() {
    when(git.getLastCommitMessage()).thenReturn(HELLO_WORLD);
    GerritChange gerritChange = tested.setAndGetChange(BranchShortName.MASTER);

    when(changeChangeId.toString()).thenReturn("I1234");
    tested.createPatchSet(gerritChange, "start", "end", null);

    verify(git).commitTree(any(), any(), endsWith("\nChange-Id: I1234"));
  }

  @Test
  public void
      WHEN_create_patch_set_between_start_and_stop_THEN_commit_tree_between_start_and_end() {
    when(git.getLastCommitMessage()).thenReturn(HELLO_WORLD);
    GerritChange gerritChange = tested.setAndGetChange(BranchShortName.MASTER);
    tested.createPatchSet(gerritChange, "start", "end", null);

    verify(git).commitTree(eq("end"), eq("start"), any());
  }

  @Test
  public void
      GIVEN_commit_tree_id_foo_and_target_master_WHEN_create_patch_set_THEN_it_should_push_foo_to_ref_for_master() {
    when(git.getLastCommitMessage()).thenReturn(HELLO_WORLD);
    GerritChange gerritChange = tested.setAndGetChange(BranchShortName.MASTER);
    when(git.commitTree(any(), any(), any())).thenReturn(FOO);

    tested.createPatchSet(gerritChange, "start", "end", null);

    verify(git).push(any(), startsWith(FOO + ":refs/for/" + BranchShortName.MASTER));
  }

  @Test
  public void WHEN_create_patch_set_THEN_it_should_push_to_pushurl() {
    when(git.getLastCommitMessage()).thenReturn(HELLO_WORLD);
    GerritChange gerritChange = tested.setAndGetChange(BranchShortName.MASTER);

    tested.createPatchSet(gerritChange, "start", "end", null);

    verify(git).push(eq(pushUrl.toString()), any());
  }

  @Test
  public void
      WHEN_create_patch_set_with_subject_WHERE_IS_MY_MIND_THEN_it_suffix_push_with_WHERE_IS_MY_MIND() {
    when(git.getLastCommitMessage()).thenReturn(HELLO_WORLD);
    GerritChange gerritChange = tested.setAndGetChange(BranchShortName.MASTER);

    tested.createPatchSet(gerritChange, "start", "end", PatchSetSubject.of(WHERE_IS_MY_MIND));

    verify(git).push(any(), contains("m=" + GitUtils.encodeForGitRef(WHERE_IS_MY_MIND)));
  }
}
