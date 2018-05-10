package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitUtils;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created on 08/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultGerritChangeTest {

  private static final String WHERE_IS_MY_MIND = "Where is my mind?!";

  private static final ChangeNumericId NUMERIC_ID = ChangeNumericId.of(1234);
  private static final BranchShortName BAR_BRANCH = BranchShortName.of("bar");

  private PatchSetRepository patchSetRepository;

  private Change tested;

  @Before
  public void before() {
    patchSetRepository = mock(PatchSetRepository.class);
    ChangeFactory gerritChangeFactory =
        new DefaultChange.Factory(patchSetRepository, PushUrl.of("https://foo.com/bar"));
    tested = gerritChangeFactory.build(BAR_BRANCH, NUMERIC_ID);
  }

  @Test
  public void
      WHEN_create_patch_set_with_subject_WHERE_IS_MY_MIND_THEN_it_suffix_push_with_WHERE_IS_MY_MIND() {
    tested.createPatchSet(false, false, PatchSetSubject.of(WHERE_IS_MY_MIND), false);
    verify(patchSetRepository)
        .createPatch(any(), any(), contains("m=" + GitUtils.encodeForGitRef(WHERE_IS_MY_MIND)));
  }

  @Test
  public void
      WHEN_create_patch_set_with_publish_drafted_comments_THEN_it_should_push_with_option_publish_comment() {
    tested.createPatchSet(true, false, null, false);
    verify(patchSetRepository).createPatch(any(), any(), contains("publish-comments"));
  }

  @Test
  public void
      WHEN_create_patch_set_without_publish_drafted_comments_THEN_it_should_push_without_option_publish_comment() {
    tested.createPatchSet(false, false, null, false);
    verify(patchSetRepository).createPatch(any(), any(), not(contains("publish-comments")));
  }

  @Test
  public void WHEN_create_patch_set_with_wip_THEN_it_should_push_with_option_wip() {
    tested.createPatchSet(false, true, null, false);
    verify(patchSetRepository).createPatch(any(), any(), contains("wip"));
  }

  @Test
  public void WHEN_create_patch_set_without_wip_THEN_it_should_push_without_option_wip() {
    tested.createPatchSet(false, false, null, false);
    verify(patchSetRepository).createPatch(any(), any(), not(contains("wip")));
  }

  @Test
  public void WHEN_create_patch_set_with_bypassreview_THEN_it_should_push_with_option_submit() {
    tested.createPatchSet(false, false, null, true);
    verify(patchSetRepository).createPatch(any(), any(), contains("submit"));
  }

  @Test
  public void
      WHEN_create_patch_set_without_bypassreview_THEN_it_should_push_without_option_submit() {
    tested.createPatchSet(false, false, null, false);
    verify(patchSetRepository).createPatch(any(), any(), not(contains("submit")));
  }
}
