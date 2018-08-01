package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.RemoteName;
import com.cosium.vet.git.RevisionId;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created on 01/08/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class ChangeParentBranchTest {

  private static final BranchShortName BRANCH = BranchShortName.of("foo");

  private GitClient git;
  private ChangeParent tested;

  @Before
  public void before() {
    git = mock(GitClient.class);
    tested = new ChangeParentBranch.Factory(git).build(BRANCH);
  }

  @Test
  public void GIVEN_no_remote_for_branch_WHEN_getrevision_THEN_it_fails() {
    assertThatThrownBy(() -> tested.getRevision()).isInstanceOf(RuntimeException.class);
  }

  @Test
  public void
      WHEN_getrevision_THEN_it_returns_the_most_recent_commit_between_remote_branch_and_current() {
    RemoteName remote = RemoteName.ORIGIN;
    when(git.getRemote(BRANCH)).thenReturn(Optional.of(remote));

    RevisionId expectedRevision = RevisionId.of("bar");
    when(git.getMostRecentCommonCommit(String.format("%s/%s", remote, BRANCH)))
        .thenReturn(expectedRevision.toString());

    assertThat(tested.getRevision()).isEqualTo(expectedRevision);
  }
}
