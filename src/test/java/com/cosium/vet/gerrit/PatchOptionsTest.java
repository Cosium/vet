package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitUtils;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Created on 12/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class PatchOptionsTest {

  private static final String REVISION_ID = "revision-id";
  private static final String WHERE_IS_MY_MIND = "Where is my mind?!";
  private static final PatchSubject WHERE_IS_MY_MIND_SUBJECT =
      PatchSubject.of(WHERE_IS_MY_MIND);

  private String buildTargetOptions(PatchOptions patchOptions) {
    return patchOptions.buildGitPushTarget(REVISION_ID, BranchShortName.MASTER);
  }

  @Test
  public void WHEN_empty_THEN_push_target_has_no_option() {
    String pushTarget = PatchOptions.EMPTY.buildGitPushTarget(REVISION_ID, BranchShortName.MASTER);
    assertThat(pushTarget).isEqualTo(REVISION_ID + ":refs/for/" + BranchShortName.MASTER + "%");
  }

  @Test
  public void WHEN_option_subject_WHERE_IS_MY_MIND_THEN_it_suffix_push_with_WHERE_IS_MY_MIND() {
    PatchOptions patchOptions = PatchOptions.builder().subject(WHERE_IS_MY_MIND_SUBJECT).build();
    assertThat(buildTargetOptions(patchOptions))
        .contains("m=" + GitUtils.encodeForGitRef(WHERE_IS_MY_MIND));
  }

  @Test
  public void
      WHEN_option_publish_drafted_comments_THEN_it_should_push_with_option_publish_comment() {
    PatchOptions patchOptions = PatchOptions.builder().publishDraftComments().build();
    assertThat(buildTargetOptions(patchOptions)).contains("publish-comments");
  }

  @Test
  public void WHEN_option_wip_THEN_it_should_push_with_option_wip() {
    PatchOptions patchOptions = PatchOptions.builder().workInProgress().build();
    assertThat(buildTargetOptions(patchOptions)).contains("wip");
  }

  @Test
  public void WHEN_option_bypassreview_THEN_it_should_push_with_option_submit() {
    PatchOptions patchOptions = PatchOptions.builder().bypassReview().build();
    assertThat(buildTargetOptions(patchOptions)).contains("submit");
  }
}
