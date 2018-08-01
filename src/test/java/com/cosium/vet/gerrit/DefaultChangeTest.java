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
public class DefaultChangeTest {

  private static final PushUrl PUSH_URL = PushUrl.of("foo");
  private static final ChangeNumericId NUMERIC_ID = ChangeNumericId.of(1234);

  private PatchsetRepository patchsetRepository;
  private Change tested;

  @Before
  public void before() {
    patchsetRepository = mock(PatchsetRepository.class);

    tested = new DefaultChange.Factory(patchsetRepository, PUSH_URL).build(NUMERIC_ID);
  }

  @Test
  public void WHEN_fetch_revision_THEN_the_latest_patchset_revision() {
    RevisionId latestPatchSetRevision = RevisionId.of("foo");
    Patchset patchset = mock(Patchset.class);
    when(patchsetRepository.findLatestPatchset(NUMERIC_ID)).thenReturn(Optional.of(patchset));
    when(patchset.getRevision()).thenReturn(latestPatchSetRevision);

    assertThat(tested.fetchRevision()).isEqualTo(latestPatchSetRevision);
  }

  @Test
  public void GIVEN_no_patchset_WHEN_fetch_revision_THEN_it_fails() {
    assertThatThrownBy(() -> tested.fetchRevision()).isInstanceOf(RuntimeException.class);
  }
}
