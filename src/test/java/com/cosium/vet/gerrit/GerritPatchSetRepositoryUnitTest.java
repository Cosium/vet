package com.cosium.vet.gerrit;

import com.cosium.vet.git.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.*;

/**
 * Created on 27/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class GerritPatchSetRepositoryUnitTest {

  private static final ChangeNumericId _1081 = ChangeNumericId.of(1081);

  private static final BranchShortName BAR_BRANCH = BranchShortName.of("bar");

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

  private static final PushUrl PUSH_URL = PushUrl.of("https://foo.bar");

  private GitClient git;
  private PatchSetCommitMessageFactory patchSetCommitMessageFactory;
  private DefaultPatchSetRepository tested;

  @Before
  public void before() {
    git = mock(GitClient.class);
    when(git.getParent(any())).thenReturn(RevisionId.of("parent"));
    when(git.getRemote(BAR_BRANCH)).thenReturn(Optional.of(RemoteName.ORIGIN));
    when(git.commitTree(any(), any(), any())).thenReturn("commit");
    when(git.getMostRecentCommonCommit(any())).thenReturn("most-recent-commit");
    when(git.push(any(), any())).thenReturn("Push log");

    patchSetCommitMessageFactory = mock(PatchSetCommitMessageFactory.class);
    when(patchSetCommitMessageFactory.build(any())).thenReturn(CommitMessage.of("Hello world"));
    tested = new DefaultPatchSetRepository(git, PUSH_URL, patchSetCommitMessageFactory);
  }

  @Test
  public void
      GIVEN_refs_1048_1_with_i2222_and_1081_2_with_i1111_WHEN_retrieving_latestpatchsetcommitmessage_of_i1111_THEN_it_should_return_1081_2_commit_message() {
    when(git.listRemoteRefs(any())).thenReturn(List.of(HEAD, _1048_1, META, _1081_2));

    when(git.getCommitMessage(_1048_1.getRevisionId()))
        .thenReturn(CommitMessage.of("Bar man Change-Id: I2222"));

    when(git.getCommitMessage(_1081_2.getRevisionId()))
        .thenReturn(CommitMessage.of("Foo man Change-Id: I1111"));

    assertThat(tested.findLastestPatch(_1081))
        .hasValueSatisfying(
            patch ->
                assertThat(patch.getCommitMessage())
                    .isEqualTo(CommitMessage.of("Foo man Change-Id: I1111")));
  }

  @Test
  public void
      GIVEN_refs_1048_1_and_1048_4_with_i2222_comma_1081_2_and_1081_3_with_i1111_WHEN_retrieving_latestpatchsetcommitmessage_i1111_THEN_it_should_retrieve_1081_3_commitmessages() {
    when(git.listRemoteRefs(any()))
        .thenReturn(List.of(HEAD, _1081_3, _1048_1, META, _1081_2, _1048_4));

    when(git.getCommitMessage(_1048_1.getRevisionId()))
        .thenReturn(CommitMessage.of("Foo man Change-Id: I2222"));
    when(git.getCommitMessage(_1048_4.getRevisionId()))
        .thenReturn(CommitMessage.of("Bar man Change-Id: I2222"));

    when(git.getCommitMessage(_1081_2.getRevisionId()))
        .thenReturn(CommitMessage.of("Foo man Change-Id: I1111"));
    when(git.getCommitMessage(_1081_3.getRevisionId()))
        .thenReturn(CommitMessage.of("Bar man Change-Id: I1111"));

    assertThat(tested.findLastestPatch(_1081))
        .hasValueSatisfying(
            patch ->
                assertThat(patch.getCommitMessage())
                    .isEqualTo(CommitMessage.of("Bar man Change-Id: I1111")));
  }

  @Test
  public void
      GIVEN_refs_1048_1_with_i2222_and_1081_2_with_i2222_WHEN_retrieving_latestpatchsetcommitmessage_of_i2222_THEN_1081_2_will_be_returned() {
    when(git.listRemoteRefs(any())).thenReturn(List.of(_1048_1, _1081_2));

    when(git.getCommitMessage(_1081_2.getRevisionId()))
        .thenReturn(CommitMessage.of("Foo man Change-Id: I2222"));
    when(git.getCommitMessage(_1048_1.getRevisionId()))
        .thenReturn(CommitMessage.of("Bar man Change-Id: I2222"));

    assertThat(tested.findLastestPatch(_1081))
        .hasValueSatisfying(
            patch ->
                assertThat(patch.getCommitMessage())
                    .isEqualTo(CommitMessage.of("Foo man Change-Id: I2222")));
  }

  @Test
  public void WHEN_create_patch_set_until_end_THEN_commit_tree_be_until_end() {
    when(git.getTree()).thenReturn("end");
    tested.createPatch(BAR_BRANCH, _1081, PatchOptions.DEFAULT);
    verify(git).commitTree(eq("end"), any(), any());
  }

  @Test
  public void
      GIVEN_commit_tree_id_foo_and_target_bar_WHEN_create_patch_set_THEN_it_should_push_foo_to_ref_for_bar() {
    when(git.commitTree(any(), any(), any())).thenReturn("foo");
    tested.createPatch(BAR_BRANCH, _1081, PatchOptions.DEFAULT);
    verify(git).push(any(), startsWith("foo:refs/for/" + BAR_BRANCH));
  }

  @Test
  public void WHEN_create_patch_set_THEN_it_should_push_to_pushurl() {
    tested.createPatch(BAR_BRANCH, _1081, PatchOptions.DEFAULT);
    verify(git).push(eq(PUSH_URL.toString()), any());
  }
}
