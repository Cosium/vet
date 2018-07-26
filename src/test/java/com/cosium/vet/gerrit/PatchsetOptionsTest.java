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
public class PatchsetOptionsTest {

  private static final String REVISION_ID = "revision-id";
  private static final String WHERE_IS_MY_MIND = "Where is my mind?!";
  private static final PatchsetSubject WHERE_IS_MY_MIND_SUBJECT = PatchsetSubject.of(WHERE_IS_MY_MIND);

  private String buildTargetOptions(PatchsetOptions patchOptions) {
    return patchOptions.buildGitPushTarget(REVISION_ID, BranchShortName.MASTER);
  }

  @Test
  public void WHEN_default_THEN_push_target_has_no_option() {
    String pushTarget = PatchsetOptions.DEFAULT.buildGitPushTarget(REVISION_ID, BranchShortName.MASTER);
    assertThat(pushTarget).isEqualTo(REVISION_ID + ":refs/for/" + BranchShortName.MASTER + "%");
  }

  @Test
  public void WHEN_option_subject_WHERE_IS_MY_MIND_THEN_it_suffix_push_with_WHERE_IS_MY_MIND() {
    PatchsetOptions patchOptions = PatchsetOptions.builder().subject(WHERE_IS_MY_MIND_SUBJECT).build();
    assertThat(buildTargetOptions(patchOptions))
        .contains("m=" + GitUtils.encodeForGitRef(WHERE_IS_MY_MIND));
  }

  @Test
  public void
      WHEN_option_publish_drafted_comments_THEN_it_should_push_with_option_publish_comment() {
    PatchsetOptions patchOptions = PatchsetOptions.builder().publishDraftComments().build();
    assertThat(buildTargetOptions(patchOptions)).contains("publish-comments");
  }

  @Test
  public void WHEN_option_wip_THEN_it_should_push_with_option_wip() {
    PatchsetOptions patchOptions = PatchsetOptions.builder().workInProgress().build();
    assertThat(buildTargetOptions(patchOptions)).contains("wip");
  }

  @Test
  public void WHEN_option_bypassreview_THEN_it_should_push_with_option_submit() {
    PatchsetOptions patchOptions = PatchsetOptions.builder().bypassReview().build();
    assertThat(buildTargetOptions(patchOptions)).contains("submit");
  }
}
