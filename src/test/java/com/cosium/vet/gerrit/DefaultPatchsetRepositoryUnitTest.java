package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchRef;
import com.cosium.vet.git.BranchRefName;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.CommitMessage;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.RevisionId;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created on 27/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultPatchsetRepositoryUnitTest {

  private static final ChangeNumericId _1081 = ChangeNumericId.of(1081);

  private static final BranchShortName TARGET_BRANCH = BranchShortName.of("bar");

  private static final BranchRef HEAD =
      new BranchRef(
          RevisionId.of("3cc707ca7a7720684bada790b5011573bee78a13"), BranchRefName.of("HEAD"));

  private static final BranchRef _1081_3 =
      new BranchRef(
          RevisionId.of("9dfc574e74ade12285512dad12172c533c5ff96f"),
          BranchRefName.of("refs/changes/81/1081/3"));
  private static final BranchRef _1081_2 =
      new BranchRef(
          RevisionId.of("9ac5ea128e339acc2e372f3df7277e316167dfe8"),
          BranchRefName.of("refs/changes/81/1081/2"));

  private static final BranchRef _1048_1 =
      new BranchRef(
          RevisionId.of("8612790edb5784abbf541dd21247cfd2fb2cd466"),
          BranchRefName.of("refs/changes/48/1048/1"));
  private static final BranchRef _1048_4 =
      new BranchRef(
          RevisionId.of("367ebe96b4d368a6d7026f3908266cd8f3889e95"),
          BranchRefName.of("refs/changes/48/1048/3"));

  private static final BranchRef META =
      new BranchRef(
          RevisionId.of("e88c2aa1f60b5c97606fbadea531eb37dd4aff99"),
          BranchRefName.of("refs/changes/81/1081/meta"));

  private static final PushUrl PUSH_URL = PushUrl.of("https://foo.bar/baz");

  private GitClient git;
  private DefaultPatchsetRepository tested;

  @Before
  public void before() {
    git = mock(GitClient.class);
    when(git.getFirstParent(any())).thenReturn(RevisionId.of("parent"));
    when(git.commitTree(any(), any(), any())).thenReturn("commit");
    when(git.push(any(), any())).thenReturn("Push log " + PUSH_URL + "/1234");

    PatchsetCommitMessageFactory patchSetCommitMessageFactory =
        mock(PatchsetCommitMessageFactory.class);
    when(patchSetCommitMessageFactory.build(any())).thenReturn(CommitMessage.of("Hello world"));
    when(patchSetCommitMessageFactory.build()).thenReturn(CommitMessage.of("Hello world"));

    TargetBranch.Factory branchFactory = mock(TargetBranch.Factory.class);
    TargetBranch targetBranch = mock(TargetBranch.class);
    when(branchFactory.build(TARGET_BRANCH)).thenReturn(targetBranch);
    when(targetBranch.computeChangeStartRevision())
        .thenReturn(RevisionId.of("most-recent-commit"));

    tested =
        new DefaultPatchsetRepository(git, PUSH_URL, patchSetCommitMessageFactory, branchFactory);
  }

  @Test
  public void
      GIVEN_refs_1048_1_with_i2222_and_1081_2_with_i1111_WHEN_retrieving_latestpatchsetcommitmessage_of_i1111_THEN_it_should_return_1081_2_commit_message() {
    when(git.listRemoteRefs(any())).thenReturn(Arrays.asList(HEAD, _1048_1, META, _1081_2));

    when(git.getCommitMessage(_1048_1.getRevisionId()))
        .thenReturn(CommitMessage.of("Bar man Change-Id: I2222"));

    when(git.getCommitMessage(_1081_2.getRevisionId()))
        .thenReturn(CommitMessage.of("Foo man Change-Id: I1111"));

    assertThat(tested.findLatestPatchset(_1081))
        .hasValueSatisfying(
            patch ->
                assertThat(patch.getCommitMessage())
                    .isEqualTo(CommitMessage.of("Foo man Change-Id: I1111")));
  }

  @Test
  public void
      GIVEN_refs_1048_1_and_1048_4_with_i2222_comma_1081_2_and_1081_3_with_i1111_WHEN_retrieving_latestpatchsetcommitmessage_i1111_THEN_it_should_retrieve_1081_3_commitmessages() {
    when(git.listRemoteRefs(any()))
        .thenReturn(Arrays.asList(HEAD, _1081_3, _1048_1, META, _1081_2, _1048_4));

    when(git.getCommitMessage(_1048_1.getRevisionId()))
        .thenReturn(CommitMessage.of("Foo man Change-Id: I2222"));
    when(git.getCommitMessage(_1048_4.getRevisionId()))
        .thenReturn(CommitMessage.of("Bar man Change-Id: I2222"));

    when(git.getCommitMessage(_1081_2.getRevisionId()))
        .thenReturn(CommitMessage.of("Foo man Change-Id: I1111"));
    when(git.getCommitMessage(_1081_3.getRevisionId()))
        .thenReturn(CommitMessage.of("Bar man Change-Id: I1111"));

    assertThat(tested.findLatestPatchset(_1081))
        .hasValueSatisfying(
            patch ->
                assertThat(patch.getCommitMessage())
                    .isEqualTo(CommitMessage.of("Bar man Change-Id: I1111")));
  }

  @Test
  public void
      GIVEN_refs_1048_1_with_i2222_and_1081_2_with_i2222_WHEN_retrieving_latestpatchsetcommitmessage_of_i2222_THEN_1081_2_will_be_returned() {
    when(git.listRemoteRefs(any())).thenReturn(Arrays.asList(_1048_1, _1081_2));

    when(git.getCommitMessage(_1081_2.getRevisionId()))
        .thenReturn(CommitMessage.of("Foo man Change-Id: I2222"));
    when(git.getCommitMessage(_1048_1.getRevisionId()))
        .thenReturn(CommitMessage.of("Bar man Change-Id: I2222"));

    assertThat(tested.findLatestPatchset(_1081))
        .hasValueSatisfying(
            patch ->
                assertThat(patch.getCommitMessage())
                    .isEqualTo(CommitMessage.of("Foo man Change-Id: I2222")));
  }

  @Test
  public void WHEN_create_patch_set_until_end_THEN_commit_tree_be_until_end() {
    when(git.listRemoteRefs(any())).thenReturn(Collections.singletonList(_1081_2));
    when(git.getCommitMessage(_1081_2.getRevisionId()))
        .thenReturn(CommitMessage.of("Foo man Change-Id: I1111"));

    when(git.getTree()).thenReturn("end");
    tested.createPatchset(TARGET_BRANCH, _1081, PatchsetOptions.DEFAULT);
    verify(git).commitTree(eq("end"), any(), any());
  }

  @Test
  public void
      GIVEN_commit_tree_id_foo_and_target_bar_WHEN_create_patch_set_THEN_it_should_push_foo_to_ref_for_bar() {
    when(git.listRemoteRefs(any())).thenReturn(Collections.singletonList(_1081_2));
    when(git.getCommitMessage(_1081_2.getRevisionId()))
        .thenReturn(CommitMessage.of("Foo man Change-Id: I1111"));

    when(git.commitTree(any(), any(), any())).thenReturn("foo");
    tested.createPatchset(TARGET_BRANCH, _1081, PatchsetOptions.DEFAULT);
    verify(git).push(any(), startsWith("foo:refs/for/" + TARGET_BRANCH));
  }

  @Test
  public void WHEN_create_patch_set_THEN_it_should_push_to_pushurl() {
    when(git.listRemoteRefs(any())).thenReturn(Collections.singletonList(_1081_2));
    when(git.getCommitMessage(_1081_2.getRevisionId()))
        .thenReturn(CommitMessage.of("Foo man Change-Id: I1111"));

    tested.createPatchset(TARGET_BRANCH, _1081, PatchsetOptions.DEFAULT);
    verify(git).push(eq(PUSH_URL.toString()), any());
  }

  @Test
  public void
      WHEN_createChangeFirstPatchset_THEN_it_push_a_diff_between_the_target_branch_and_the_tree() {
    tested.createChangeFirstPatchset(TARGET_BRANCH, PatchsetOptions.DEFAULT);

    verify(git).push(eq(PUSH_URL.toString()), any());
  }
}
