package com.cosium.vet.push;

import com.cosium.vet.gerrit.GerritChange;
import com.cosium.vet.gerrit.GerritClient;
import com.cosium.vet.gerrit.GerritClientFactory;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.GitClientFactory;
import com.cosium.vet.git.RemoteName;
import com.cosium.vet.runtime.UserInput;
import org.junit.Before;
import org.junit.Test;

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
  private GerritClient gerrit;
  private UserInput userInput;

  private PushCommandFactory factory;

  @Before
  public void before() {
    git = mock(GitClient.class);
    gerrit = mock(GerritClient.class);
    userInput = mock(UserInput.class);

    GitClientFactory gitClientFactory = mock(GitClientFactory.class);
    when(gitClientFactory.build()).thenReturn(git);
    GerritClientFactory gerritClientFactory = mock(GerritClientFactory.class);
    when(gerritClientFactory.build()).thenReturn(gerrit);

    factory = new PushCommand.Factory(gitClientFactory, gerritClientFactory, userInput);

    when(git.getRemote(any())).thenReturn(Optional.of(REMOTE));
  }

  @Test
  public void GIVEN_cached_change_WHEN_pushing_THEN_it_should_not_set_change() {
    PushCommand pushCommand = factory.build(null, null, null, null, null);
    when(gerrit.getChange()).thenReturn(Optional.of(mock(GerritChange.class)));
    pushCommand.execute();
    verify(gerrit, times(0)).setChange(BranchShortName.of("foo"));
  }

  @Test
  public void
      GIVEN_cached_change_WHEN_pushin_to_target_foo_branch_THEN_it_should_set_foo_in_gerrit_client() {
    PushCommand pushCommand = factory.build(BranchShortName.of("foo"), null, null, null, null);
    when(gerrit.setChange(any())).thenReturn(mock(GerritChange.class));
    pushCommand.execute();
    verify(gerrit).setChange(BranchShortName.of("foo"));
  }
}
