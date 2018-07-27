package com.cosium.vet.command.push;

import com.cosium.vet.gerrit.Change;
import com.cosium.vet.gerrit.ChangeRepository;
import com.cosium.vet.gerrit.ChangeRepositoryFactory;
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

  private ChangeRepository changeRepository;
  private UserOutput userOutput;

  private PushCommandFactory factory;

  @Before
  public void before() {
    ChangeRepositoryFactory changeRepositoryFactory = mock(ChangeRepositoryFactory.class);
    changeRepository = mock(ChangeRepository.class);
    when(changeRepositoryFactory.build()).thenReturn(changeRepository);
    Change change = mock(Change.class);
    when(changeRepository.getTrackedChange()).thenReturn(Optional.of(change));

    userOutput = mock(UserOutput.class);

    factory = new PushCommand.Factory(changeRepositoryFactory, userOutput);
  }

  @Test
  public void WHEN_no_tracked_change_THEN_it_display_a_message_and_does_nothing() {
    PushCommand pushCommand = factory.build(null, null, null, null);
    pushCommand.execute();
    verify(userOutput).display(anyString());
  }
}
