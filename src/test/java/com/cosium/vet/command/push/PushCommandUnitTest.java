package com.cosium.vet.command.push;

import com.cosium.vet.gerrit.ChangeRepository;
import com.cosium.vet.gerrit.ChangeRepositoryFactory;
import com.cosium.vet.git.RemoteName;
import com.cosium.vet.runtime.UserOutput;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Created on 08/03/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class PushCommandUnitTest {

  private static final RemoteName REMOTE = RemoteName.ORIGIN;

  private ChangeRepository gerrit;
  private UserOutput userOutput;

  private PushCommandFactory factory;

  @Before
  public void before() {
    gerrit = mock(ChangeRepository.class);
    userOutput = mock(UserOutput.class);

    ChangeRepositoryFactory gerritClientFactory = mock(ChangeRepositoryFactory.class);
    when(gerritClientFactory.build()).thenReturn(gerrit);

    factory = new PushCommand.Factory(gerritClientFactory, userOutput);
  }

  @Test
  public void WHEN_no_tracked_change_THEN_it_display_a_message_and_does_nothing() {
    PushCommand pushCommand = factory.build(null, null, null, null);
    pushCommand.execute();
    verify(userOutput).display(anyString());
  }
}
