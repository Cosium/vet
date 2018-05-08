package com.cosium.vet.push;

import com.cosium.vet.gerrit.ChangeNumericId;
import com.cosium.vet.gerrit.GerritChange;
import com.cosium.vet.gerrit.GerritChangeRepository;
import com.cosium.vet.gerrit.GerritChangeRepositoryFactory;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.GitClientFactory;
import com.cosium.vet.git.RemoteName;
import com.cosium.vet.runtime.UserInput;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * Created on 08/03/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class PushCommandUnitTest {

  private static final RemoteName REMOTE = RemoteName.ORIGIN;

  private GitClient git;
  private GerritChangeRepository gerrit;
  private UserInput userInput;

  private PushCommandFactory factory;

  @Before
  public void before() {
    git = mock(GitClient.class);
    gerrit = mock(GerritChangeRepository.class);
    userInput = mock(UserInput.class);

    GitClientFactory gitClientFactory = mock(GitClientFactory.class);
    when(gitClientFactory.build()).thenReturn(git);
    GerritChangeRepositoryFactory gerritClientFactory = mock(GerritChangeRepositoryFactory.class);
    when(gerritClientFactory.build()).thenReturn(gerrit);

    factory = new PushCommand.Factory(gitClientFactory, gerritClientFactory, userInput);

    when(git.getRemote(any())).thenReturn(Optional.of(REMOTE));
  }

  @Test
  public void GIVEN_cached_change_WHEN_pushing_THEN_it_should_not_set_change() {
    PushCommand pushCommand = factory.build(null, null, null, null, null);
    when(gerrit.getTrackedChange()).thenReturn(Optional.of(mock(GerritChange.class)));
    pushCommand.execute();
    verify(gerrit, times(0)).trackChange(any());
  }

  @Test
  public void
      GIVEN_cached_change_WHEN_pushing_to_target_foo_branch_THEN_it_should_set_foo_in_gerrit_client() {
    PushCommand pushCommand = factory.build(ChangeNumericId.of(1234), null, null, null, null);
    when(gerrit.trackChange(any())).thenReturn(mock(GerritChange.class));
    pushCommand.execute();
    verify(gerrit).trackChange(ChangeNumericId.of(1234));
  }

  @Test
  public void
      WHEN_pushing_to_target_foo_branch_THEN_it_should_fetch_it_before_computing_mostrecentcommoncommit() {
    PushCommand pushCommand = factory.build(null, null, null, null, null);
    GerritChange gerritChange = mock(GerritChange.class);
    when(gerritChange.getTargetBranch()).thenReturn(BranchShortName.of("foo"));
    when(gerrit.getTrackedChange()).thenReturn(Optional.of(gerritChange));

    pushCommand.execute();

    InOrder inOrder = inOrder(git);
    inOrder.verify(git).fetch(REMOTE, BranchShortName.of("foo"));
    inOrder.verify(git).getMostRecentCommonCommit("origin/foo");
  }
}
