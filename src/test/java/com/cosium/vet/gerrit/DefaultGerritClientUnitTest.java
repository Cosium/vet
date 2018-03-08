package com.cosium.vet.gerrit;

import com.cosium.vet.VetVersion;
import com.cosium.vet.gerrit.config.GerritConfiguration;
import com.cosium.vet.gerrit.config.GerritConfigurationRepository;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.CommitMessage;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.GitUtils;
import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalMatchers.not;
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
  private static final String SOURCE_BRANCH = "feature/a";

  private AtomicReference<GerritConfiguration> lastSavedConfiguration;

  private GerritConfiguration gerritConfiguration;
  private ChangeChangeId changeChangeId;
  private DefaultGerritPatchSetRepository patchSetRepository;
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
    when(git.getBranch()).thenReturn(BranchShortName.of(SOURCE_BRANCH));

    pushUrl = GerritPushUrl.of("https://bar.com/foo");

    patchSetRepository = mock(DefaultGerritPatchSetRepository.class);
    tested =
        new DefaultGerritClient(
            configurationRepository, changeChangeIdFactory, patchSetRepository, git, pushUrl);
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
    assertThat(tested.setChange(BranchShortName.MASTER).getTargetBranch())
        .isEqualTo(BranchShortName.MASTER);
  }

  @Test
  public void WHEN_set_get_change_to_master_THEN_it_should_store_master_in_configuration() {
    tested.setChange(BranchShortName.MASTER);

    assertThat(lastSavedConfiguration.get()).isNotNull();
    verify(lastSavedConfiguration.get()).setChangeTargetBranch(BranchShortName.MASTER);
  }

  @Test
  public void
      GIVEN_last_commit_message_hello_world_WHEN_create_patch_set_THEN_commit_tree_message_should_contain_hello_world() {
    when(git.getLastCommitMessage()).thenReturn(CommitMessage.of(HELLO_WORLD));
    GerritChange gerritChange = tested.setChange(BranchShortName.MASTER);

    tested.createPatchSet(gerritChange, "start", "end", false, null);

    verify(git).commitTree(any(), any(), contains(HELLO_WORLD));
  }

  @Test
  public void
      GIVEN_changeid_I1234_WHEN_create_patch_set_THEN_commit_tree_message_should_end_with_I1234() {
    when(git.getLastCommitMessage()).thenReturn(CommitMessage.of(HELLO_WORLD));
    GerritChange gerritChange = tested.setChange(BranchShortName.MASTER);

    when(changeChangeId.toString()).thenReturn("I1234");
    tested.createPatchSet(gerritChange, "start", "end", false, null);

    verify(git).commitTree(any(), any(), endsWith("\nChange-Id: I1234"));
  }

  @Test
  public void
      GIVEN_existing_change_WHEN_create_patch_set_THEN_commit_tree_message_should_contains_SOURCE_BRANCH() {
    when(git.getLastCommitMessage()).thenReturn(CommitMessage.of(HELLO_WORLD));
    GerritChange gerritChange = tested.setChange(BranchShortName.MASTER);

    tested.createPatchSet(gerritChange, "start", "end", false, null);

    verify(git).commitTree(any(), any(), contains("\nSource-Branch: " + SOURCE_BRANCH));
  }

  @Test
  public void
      GIVEN_existing_change_WHEN_create_patch_set_THEN_commit_tree_message_should_contains_VET_VERSION() {
    when(git.getLastCommitMessage()).thenReturn(CommitMessage.of(HELLO_WORLD));
    GerritChange gerritChange = tested.setChange(BranchShortName.MASTER);

    tested.createPatchSet(gerritChange, "start", "end", false, null);

    verify(git).commitTree(any(), any(), contains("\nVet-Version: " + VetVersion.VALUE));
  }

  @Test
  public void
      GIVEN_existing_change_WHEN_create_patch_set_THEN_commit_tree_message_should_contains_exactly_one_SOURCE_BRANCH() {
    when(git.getLastCommitMessage()).thenReturn(CommitMessage.of("Source-Branch: foo"));
    GerritChange gerritChange = tested.setChange(BranchShortName.MASTER);

    tested.createPatchSet(gerritChange, "start", "end", false, null);

    ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
    verify(git).commitTree(any(), any(), messageCaptor.capture());

    String message = messageCaptor.getValue();
    assertThat(message).contains("\nSource-Branch: " + SOURCE_BRANCH);
    assertThat(StringUtils.countMatches(message, "Source-Branch")).isEqualTo(1);
  }

  @Test
  public void
      WHEN_create_patch_set_between_start_and_stop_THEN_commit_tree_between_start_and_end() {
    when(git.getLastCommitMessage()).thenReturn(CommitMessage.of(HELLO_WORLD));
    GerritChange gerritChange = tested.setChange(BranchShortName.MASTER);
    tested.createPatchSet(gerritChange, "start", "end", false, null);

    verify(git).commitTree(eq("end"), eq("start"), any());
  }

  @Test
  public void
      GIVEN_commit_tree_id_foo_and_target_master_WHEN_create_patch_set_THEN_it_should_push_foo_to_ref_for_master() {
    when(git.getLastCommitMessage()).thenReturn(CommitMessage.of(HELLO_WORLD));
    GerritChange gerritChange = tested.setChange(BranchShortName.MASTER);
    when(git.commitTree(any(), any(), any())).thenReturn(FOO);

    tested.createPatchSet(gerritChange, "start", "end", false, null);

    verify(git).push(any(), startsWith(FOO + ":refs/for/" + BranchShortName.MASTER));
  }

  @Test
  public void WHEN_create_patch_set_THEN_it_should_push_to_pushurl() {
    when(git.getLastCommitMessage()).thenReturn(CommitMessage.of(HELLO_WORLD));
    GerritChange gerritChange = tested.setChange(BranchShortName.MASTER);

    tested.createPatchSet(gerritChange, "start", "end", false, null);

    verify(git).push(eq(pushUrl.toString()), any());
  }

  @Test
  public void
      WHEN_create_patch_set_with_subject_WHERE_IS_MY_MIND_THEN_it_suffix_push_with_WHERE_IS_MY_MIND() {
    when(git.getLastCommitMessage()).thenReturn(CommitMessage.of(HELLO_WORLD));
    GerritChange gerritChange = tested.setChange(BranchShortName.MASTER);

    tested.createPatchSet(
        gerritChange, "start", "end", false, PatchSetSubject.of(WHERE_IS_MY_MIND));

    verify(git).push(any(), contains("m=" + GitUtils.encodeForGitRef(WHERE_IS_MY_MIND)));
  }

  @Test
  public void WHEN_setChange_to_SOURCE_BRANCH_THEN_it_should_fail() {
    assertThatThrownBy(() -> tested.setChange(BranchShortName.of(SOURCE_BRANCH)))
        .hasMessage("Target branch can't be the same as the current branch");
  }

  @Test
  public void
      WHEN_create_patch_set_with_publish_drafted_comments_THEN_it_should_push_with_option_publish_comment() {
    when(git.getLastCommitMessage()).thenReturn(CommitMessage.of(HELLO_WORLD));
    GerritChange gerritChange = tested.setChange(BranchShortName.MASTER);

    tested.createPatchSet(gerritChange, "start", "end", true, null);

    verify(git).push(any(), contains("publish-comments"));
  }

  @Test
  public void
      WHEN_create_patch_set_without_publish_drafted_comments_THEN_it_should_not_push_with_option_publish_comment() {
    when(git.getLastCommitMessage()).thenReturn(CommitMessage.of(HELLO_WORLD));
    GerritChange gerritChange = tested.setChange(BranchShortName.MASTER);

    tested.createPatchSet(gerritChange, "start", "end", false, null);

    verify(git).push(any(), not(contains("publish-comments")));
  }
}
