package com.cosium.vet.command;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created on 26/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class CompositeCommandArgParserUnitTest {

  private static final String EXECUTABLE_NAME = "vet";
  private static final String RUN_COMMAND_NAME = "run";

  private VetCommand vetCommand;
  private VetAdvancedCommandArgParser runParser;
  private CompositeCommandArgParser tested;

  @Before
  public void before() {
    runParser = mock(VetAdvancedCommandArgParser.class);
    when(runParser.getCommandArgName()).thenReturn(RUN_COMMAND_NAME);
    when(runParser.canParse(RUN_COMMAND_NAME)).thenReturn(true);
    when(runParser.canParse(eq(RUN_COMMAND_NAME), any())).thenReturn(true);
    when(runParser.canParse(eq(RUN_COMMAND_NAME), any(), any())).thenReturn(true);
    vetCommand = mock(VetCommand.class);
    when(runParser.parse(any())).thenReturn(vetCommand);
    tested =
        new CompositeCommandArgParser(
            "vet",
            List.of(runParser),
            new DebugOptions(List.of("--stacktrace"), List.of("--verbose")));
  }

  @Test
  public void testHelp() {
    tested.parse().execute();
  }

  @Test
  public void testCommandParse() {
    assertThat(tested.parse(RUN_COMMAND_NAME)).isSameAs(vetCommand);
  }

  @Test
  public void testCommandHelp() {
    tested.parse(RUN_COMMAND_NAME, "--help").execute();
    verify(runParser).displayHelp(EXECUTABLE_NAME);
  }

  @Test
  public void testVersion() {
    tested.parse("--version").execute();
  }

  @Test
  public void testList() {
    tested.parse("--command-list").execute();
  }
}
