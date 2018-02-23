package com.cosium.vet.push;

import com.cosium.vet.VetCommandArgParser;
import com.cosium.vet.gerrit.PatchSetSubject;
import com.cosium.vet.git.BranchShortName;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

/**
 * Created on 23/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class PushCommandArgParserUnitTest {

  private PushCommandFactory factory;
  private VetCommandArgParser tested;

  @Before
  public void before() {
    factory = mock(PushCommandFactory.class);
    when(factory.build(any(), any())).thenReturn(mock(PushCommand.class));
    tested = new PushCommandArgParser(factory);
  }

  @Test
  public void testNonPushCommand() {
    assertThat(tested.parse("help")).isEmpty();
  }

  @Test
  public void testZeroArg() {
    assertThat(tested.parse()).isEmpty();
  }

  @Test
  public void testTargetBranchShort() {
    tested.parse("push", "-b", "b1");
    verify(factory).build(eq(BranchShortName.of("b1")), isNull());
  }

  @Test
  public void testTargetBranchLong() {
    tested.parse("push", "--target-branch", "b1");
    verify(factory).build(eq(BranchShortName.of("b1")), isNull());
  }

  @Test
  public void testPatchSetSubjectShort() {
    tested.parse("push", "-s", "hello");
    verify(factory).build(isNull(), eq(PatchSetSubject.of("hello")));
  }

  @Test
  public void testPatchSetSubjectLong() {
    tested.parse("push", "--patch-set-subject", "hello");
    verify(factory).build(isNull(), eq(PatchSetSubject.of("hello")));
  }

  @Test
  public void testAll() {
    tested.parse("push", "--target-branch", "b1", "--patch-set-subject", "hello");
    verify(factory).build(eq(BranchShortName.of("b1")), eq(PatchSetSubject.of("hello")));
  }
}
