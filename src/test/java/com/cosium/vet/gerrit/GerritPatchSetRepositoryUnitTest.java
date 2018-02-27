package com.cosium.vet.gerrit;

import com.cosium.vet.git.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created on 27/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class GerritPatchSetRepositoryUnitTest {

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

  private static final GerritPushUrl PUSH_URL = GerritPushUrl.of("https://foo.bar");

  private GitClient gitClient;
  private DefaultGerritPatchSetRepository tested;

  @Before
  public void before() {
    gitClient = mock(GitClient.class);
    tested = new DefaultGerritPatchSetRepository(gitClient);
  }

  @Test
  public void testGetLastestPatchSetCommitMessage1() {
    when(gitClient.listRemoteRefs(any())).thenReturn(List.of(HEAD, _1048_1, META, _1081_2));

    when(gitClient.getCommitMessage(_1048_1.getRevisionId()))
        .thenReturn(CommitMessage.of("Bar man Change-Id: I2222"));

    ChangeChangeId changeChangeId = mock(ChangeChangeId.class);
    when(changeChangeId.toString()).thenReturn("I1111");
    when(gitClient.getCommitMessage(_1081_2.getRevisionId()))
        .thenReturn(CommitMessage.of("Foo man Change-Id: I1111"));

    assertThat(tested.getLastestPatchSetCommitMessage(PUSH_URL, changeChangeId))
        .contains(CommitMessage.of("Foo man Change-Id: I1111"));
  }

  @Test
  public void testGetLastestPatchSetCommitMessage_1048_4_and_1081_3() {
    when(gitClient.listRemoteRefs(any()))
        .thenReturn(List.of(HEAD, _1081_3, _1048_1, META, _1081_2, _1048_4));

    when(gitClient.getCommitMessage(_1048_1.getRevisionId()))
        .thenReturn(CommitMessage.of("Foo man Change-Id: I2222"));
    when(gitClient.getCommitMessage(_1048_4.getRevisionId()))
        .thenReturn(CommitMessage.of("Bar man Change-Id: I2222"));

    ChangeChangeId changeChangeId = mock(ChangeChangeId.class);
    when(changeChangeId.toString()).thenReturn("I1111");
    when(gitClient.getCommitMessage(_1081_2.getRevisionId()))
        .thenReturn(CommitMessage.of("Foo man Change-Id: I1111"));
    when(gitClient.getCommitMessage(_1081_3.getRevisionId()))
        .thenReturn(CommitMessage.of("Bar man Change-Id: I1111"));

    assertThat(tested.getLastestPatchSetCommitMessage(PUSH_URL, changeChangeId))
        .contains(CommitMessage.of("Bar man Change-Id: I1111"));
  }
}
