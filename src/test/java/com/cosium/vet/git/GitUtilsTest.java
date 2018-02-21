package com.cosium.vet.git;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class GitUtilsTest {

    @Test
    public void testEncode(){
        assertThat(GitUtils.encodeForGitRef("This is a rebase on master!")).isEqualTo("This_is_a_rebase_on_master%21");
    }

}