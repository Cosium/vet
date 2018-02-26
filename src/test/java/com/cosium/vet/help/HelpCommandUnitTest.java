package com.cosium.vet.help;

import com.cosium.vet.VetCommandArgParser;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created on 26/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class HelpCommandUnitTest {

  private static final String EXECUTABLE_NAME = "vet";
  private static final String STD_COMMAND_NAME = "run";

  private VetCommandArgParser stdParser;
  private HelpCommand.ArgParser tested;

  @Before
  public void before() {
    stdParser = mock(VetCommandArgParser.class);
    when(stdParser.getCommandArgName()).thenReturn(STD_COMMAND_NAME);
    tested = new HelpCommand.ArgParser("vet", List.of(stdParser));
  }

  @Test
  public void testMainHelp() {
    tested.parse().execute();
  }

  @Test
  public void testCommandHelp() {
    tested.parse(STD_COMMAND_NAME).execute();
    verify(stdParser).displayHelp(EXECUTABLE_NAME);
  }
}
