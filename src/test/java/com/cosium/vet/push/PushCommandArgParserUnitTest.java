package com.cosium.vet.push;

import com.cosium.vet.command.VetAdvancedCommandArgParser;
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
  private VetAdvancedCommandArgParser tested;

  @Before
  public void before() {
    factory = mock(PushCommandFactory.class);
    when(factory.build(any(), any(), any(), any())).thenReturn(mock(PushCommand.class));
    tested = new PushCommandArgParser(factory);
  }

  @Test
  public void testNonPushCommand() {
    assertThat(tested.canParse("help")).isFalse();
  }

  @Test
  public void testZeroArg() {
    assertThat(tested.canParse()).isFalse();
  }

  @Test
  public void testTargetBranchShort() {
    tested.parse("push", "-b", "b1");
    verify(factory).build(eq(BranchShortName.of("b1")), isNull(), isNull(), isNull());
  }

  @Test
  public void testTargetBranchLong() {
    tested.parse("push", "--target-branch", "b1");
    verify(factory).build(eq(BranchShortName.of("b1")), isNull(), isNull(), isNull());
  }

  @Test
  public void testPatchSetSubjectShort() {
    tested.parse("push", "-s", "hello");
    verify(factory).build(isNull(), isNull(), isNull(), eq(PatchSetSubject.of("hello")));
  }

  @Test
  public void testPatchSetSubjectLong() {
    tested.parse("push", "--patch-set-subject", "hello");
    verify(factory).build(isNull(), isNull(), isNull(), eq(PatchSetSubject.of("hello")));
  }

  @Test
  public void testPublishDraftedCommentsShort() {
    tested.parse("push", "-p");
    verify(factory).build(isNull(), eq(true), isNull(), isNull());
  }

  @Test
  public void testPublishDraftedCommentsLong() {
    tested.parse("push", "--publish-drafted-comments");
    verify(factory).build(isNull(), eq(true), isNull(), isNull());
  }

  @Test
  public void testWipShort() {
    tested.parse("push", "-w");
    verify(factory).build(isNull(), isNull(), eq(true), isNull());
  }

  @Test
  public void testWipLong() {
    tested.parse("push", "--work-in-progress");
    verify(factory).build(isNull(), isNull(), eq(true), isNull());
  }

  @Test
  public void testAll() {
    tested.parse(
        "push",
        "--target-branch",
        "b1",
        "--patch-set-subject",
        "hello",
        "--publish-drafted-comments",
        "--work-in-progress");
    verify(factory)
        .build(eq(BranchShortName.of("b1")), eq(true), eq(true), eq(PatchSetSubject.of("hello")));
  }

  @Test
  public void displayHelp() {
    tested.displayHelp("vet");
  }
}
