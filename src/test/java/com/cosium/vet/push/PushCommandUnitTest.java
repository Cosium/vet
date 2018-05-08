package com.cosium.vet.push;

import com.cosium.vet.gerrit.GerritChangeRepository;
import com.cosium.vet.gerrit.GerritChangeRepositoryFactory;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.GitClientFactory;
import com.cosium.vet.git.RemoteName;
import com.cosium.vet.runtime.UserOutput;
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
  private GerritChangeRepository gerrit;
  private UserOutput userOutput;

  private PushCommandFactory factory;

  @Before
  public void before() {
    git = mock(GitClient.class);
    gerrit = mock(GerritChangeRepository.class);
    userOutput = mock(UserOutput.class);

    GitClientFactory gitClientFactory = mock(GitClientFactory.class);
    when(gitClientFactory.build()).thenReturn(git);
    GerritChangeRepositoryFactory gerritClientFactory = mock(GerritChangeRepositoryFactory.class);
    when(gerritClientFactory.build()).thenReturn(gerrit);

    factory = new PushCommand.Factory(gitClientFactory, gerritClientFactory, userOutput);

    when(git.getRemote(any())).thenReturn(Optional.of(REMOTE));
  }

  @Test
  public void WHEN_no_tracked_change_THEN_it_display_a_message_and_does_nothing() {
    PushCommand pushCommand = factory.build(null, null, null, null);
    pushCommand.execute();
    verify(userOutput).display(anyString());
  }
}
