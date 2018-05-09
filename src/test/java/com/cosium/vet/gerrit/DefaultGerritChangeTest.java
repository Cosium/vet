package com.cosium.vet.gerrit;

import com.cosium.vet.git.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.*;

/**
 * Created on 08/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultGerritChangeTest {

  private static final String FOO = "foo";
  private static final String WHERE_IS_MY_MIND = "Where is my mind?!";

  private static final GerritPushUrl PUSH_URL = GerritPushUrl.of("https://foo.com/bar");
  private static final ChangeNumericId NUMERIC_ID = ChangeNumericId.of(1234);
  private static final BranchShortName BAR_BRANCH = BranchShortName.of("bar");

  private GitClient git;
  private PatchSetCommitMessageFactory patchSetCommitMessageFactory;

  private GerritChange tested;

  @Before
  public void before() {
    git = mock(GitClient.class);

    patchSetCommitMessageFactory = mock(PatchSetCommitMessageFactory.class);

    GerritChangeFactory gerritChangeFactory =
        new DefaultGerritChange.Factory(git, patchSetCommitMessageFactory, PUSH_URL);

    when(git.getRemote(BAR_BRANCH)).thenReturn(Optional.of(RemoteName.ORIGIN));
    when(patchSetCommitMessageFactory.build(NUMERIC_ID))
        .thenReturn(CommitMessage.of("Hello world"));
    tested = gerritChangeFactory.build(BAR_BRANCH, NUMERIC_ID);
  }

  @Test
  public void WHEN_create_patch_set_until_end_THEN_commit_tree_be_until_end() {
    when(git.getTree()).thenReturn("end");
    tested.createPatchSet(false, false, null, false);
    verify(git).commitTree(eq("end"), any(), any());
  }

  @Test
  public void
      GIVEN_commit_tree_id_foo_and_target_bar_WHEN_create_patch_set_THEN_it_should_push_foo_to_ref_for_bar() {
    when(git.commitTree(any(), any(), any())).thenReturn(FOO);
    tested.createPatchSet(false, false, null, false);
    verify(git).push(any(), startsWith(FOO + ":refs/for/" + BAR_BRANCH));
  }

  @Test
  public void WHEN_create_patch_set_THEN_it_should_push_to_pushurl() {
    tested.createPatchSet(false, false, null, false);
    verify(git).push(eq(PUSH_URL.toString()), any());
  }

  @Test
  public void
      WHEN_create_patch_set_with_subject_WHERE_IS_MY_MIND_THEN_it_suffix_push_with_WHERE_IS_MY_MIND() {
    tested.createPatchSet(false, false, PatchSetSubject.of(WHERE_IS_MY_MIND), false);
    verify(git).push(any(), contains("m=" + GitUtils.encodeForGitRef(WHERE_IS_MY_MIND)));
  }

  @Test
  public void
      WHEN_create_patch_set_with_publish_drafted_comments_THEN_it_should_push_with_option_publish_comment() {
    tested.createPatchSet(true, false, null, false);
    verify(git).push(any(), contains("publish-comments"));
  }

  @Test
  public void
      WHEN_create_patch_set_without_publish_drafted_comments_THEN_it_should_push_without_option_publish_comment() {
    tested.createPatchSet(false, false, null, false);
    verify(git).push(any(), not(contains("publish-comments")));
  }

  @Test
  public void WHEN_create_patch_set_with_wip_THEN_it_should_push_with_option_wip() {
    tested.createPatchSet(false, true, null, false);
    verify(git).push(any(), contains("wip"));
  }

  @Test
  public void WHEN_create_patch_set_without_wip_THEN_it_should_push_without_option_wip() {
    tested.createPatchSet(false, false, null, false);
    verify(git).push(any(), not(contains("wip")));
  }

  @Test
  public void WHEN_create_patch_set_with_bypassreview_THEN_it_should_push_with_option_submit() {
    tested.createPatchSet(false, false, null, true);
    verify(git).push(any(), contains("submit"));
  }

  @Test
  public void
      WHEN_create_patch_set_without_bypassreview_THEN_it_should_push_without_option_submit() {
    tested.createPatchSet(false, false, null, false);
    verify(git).push(any(), not(contains("submit")));
  }
}
