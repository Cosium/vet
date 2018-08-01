package com.cosium.vet.gerrit;

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
public class ChangeParentChangeTest {

  private static final ChangeNumericId PARENT_CHANGE_NUMERIC_ID = ChangeNumericId.of(1234);

  private ChangeRepository changeRepository;

  private ChangeParent tested;

  @Before
  public void before() {
    changeRepository = mock(ChangeRepository.class);
    ChangeRepositoryFactory changeRepositoryFactory = mock(ChangeRepositoryFactory.class);
    when(changeRepositoryFactory.build()).thenReturn(changeRepository);

    tested =
        new ChangeParentChange.Factory(changeRepositoryFactory).build(PARENT_CHANGE_NUMERIC_ID);
  }

  @Test
  public void WHEN_get_revision_THEN_it_returns_the_change_fetched_revision() {
    RevisionId fetchedRevision = RevisionId.of("foo");
    Change parentChange = mock(Change.class);
    when(changeRepository.findChange(PARENT_CHANGE_NUMERIC_ID))
        .thenReturn(Optional.of(parentChange));
    when(parentChange.fetchRevision()).thenReturn(fetchedRevision);

    assertThat(tested.getRevision()).isEqualTo(fetchedRevision);
  }

  @Test
  public void GIVEN_parent_change_not_found_WHEN_get_revision_THEN_fails() {
    assertThatThrownBy(() -> tested.getRevision()).isInstanceOf(RuntimeException.class);
  }
}
