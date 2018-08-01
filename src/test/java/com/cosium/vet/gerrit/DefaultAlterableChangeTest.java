package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.RevisionId;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created on 01/08/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultAlterableChangeTest {

  private static final BranchShortName TARGET_BRANCH = BranchShortName.of("foo");
  private static final ChangeNumericId NUMERIC_ID = ChangeNumericId.of(123);

  private Change delegate;
  private PatchsetRepository patchsetRepository;

  private AlterableChange tested;

  @Before
  public void before() {
    delegate = mock(Change.class);
    ChangeFactory changeFactory = mock(ChangeFactory.class);
    when(changeFactory.build(NUMERIC_ID)).thenReturn(delegate);
    patchsetRepository = mock(PatchsetRepository.class);

    tested =
        new DefaultAlterableChange.Factory(changeFactory, patchsetRepository)
            .build(TARGET_BRANCH, NUMERIC_ID);
  }

  @Test
  public void WHEN_create_patchset_THEN_it_returns_the_patchset_creationlog() {
    String creationLog = "log";
    PatchsetOptions options = PatchsetOptions.DEFAULT;
    CreatedPatchset createdPatchset = mock(CreatedPatchset.class);
    when(patchsetRepository.createPatchset(TARGET_BRANCH, NUMERIC_ID, options))
        .thenReturn(createdPatchset);
    when(createdPatchset.getCreationLog()).thenReturn(creationLog);

    assertThat(tested.createPatchset(options)).isEqualTo(creationLog);
  }

  @Test
  public void delegate_getNumericId() {
    ChangeNumericId numericId = ChangeNumericId.of(111);
    when(delegate.getNumericId()).thenReturn(numericId);

    assertThat(tested.getNumericId()).isSameAs(numericId);
  }

  @Test
  public void delegate_fetchRevision() {
    RevisionId revisionId = RevisionId.of("bar");
    when(delegate.fetchRevision()).thenReturn(revisionId);

    assertThat(tested.fetchRevision()).isSameAs(revisionId);
  }

  @Test
  public void delegate_fetchParent() {
    RevisionId revisionId = RevisionId.of("baz");
    when(delegate.fetchParent()).thenReturn(revisionId);

    assertThat(tested.fetchParent()).isSameAs(revisionId);
  }

  @Test
  public void delegate_getWebUrl() {
    String webUrl = "url";
    when(delegate.getWebUrl()).thenReturn(webUrl);

    assertThat(tested.getWebUrl()).isSameAs(webUrl);
  }

  @Test
  public void delegate_toString() {
    String toString = "toString";
    when(delegate.toString()).thenReturn(toString);

    assertThat(tested.toString()).isSameAs(toString);
  }
}
