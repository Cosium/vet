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
  private static final String STD_COMMAND_NAME = "run";

  private VetCommand vetCommand;
  private VetAdvancedCommandArgParser stdParser;
  private CompositeCommandArgParser tested;

  @Before
  public void before() {
    stdParser = mock(VetAdvancedCommandArgParser.class);
    when(stdParser.getCommandArgName()).thenReturn(STD_COMMAND_NAME);
    when(stdParser.canParse(STD_COMMAND_NAME)).thenReturn(true);
    when(stdParser.canParse(eq(STD_COMMAND_NAME), any())).thenReturn(true);
    when(stdParser.canParse(eq(STD_COMMAND_NAME), any(), any())).thenReturn(true);
    vetCommand = mock(VetCommand.class);
    when(stdParser.parse(any())).thenReturn(vetCommand);
    tested =
        new CompositeCommandArgParser(
            "vet",
            List.of(stdParser),
            new DebugOptions(List.of("-x", "--stacktrace"), List.of("-v", "--verbose")));
  }

  @Test
  public void testHelp() {
    tested.parse().execute();
  }

  @Test
  public void testCommandParse() {
    assertThat(tested.parse(STD_COMMAND_NAME)).isSameAs(vetCommand);
  }

  @Test
  public void testCommandHelp() {
    tested.parse(STD_COMMAND_NAME, "--help").execute();
    verify(stdParser).displayHelp(EXECUTABLE_NAME);
  }
}
