package com.cosium.vet.git;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class GitUtilsTest {

  @Test
  public void testEncode() {
    assertThat(GitUtils.encodeForGitRef("This is a rebase on master!"))
        .isEqualTo("This_is_a_rebase_on_master%21");
    assertThat(
            GitUtils.encodeForGitRef(
                "[CTR-17919] Add a webstore default payment checkbox to payment types"))
        .isEqualTo("%5BCTR-17919%5D_Add_a_webstore_default_payment_checkbox_to_payment_types");
  }
}
