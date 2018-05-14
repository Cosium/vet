package com.cosium.vet.command.autocomplete;

import com.cosium.vet.command.VetAdvancedCommandArgParser;
import com.cosium.vet.runtime.UserOutput;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Created on 12/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class AutocompleteCommandTest {

  private static final String COMMAND = "command";

  private static final String OPT_1 = "opt1";
  private static final String FULL_OPT_1 = "--" + OPT_1;
  private static final String OPT_2 = "opt2";
  private static final String FULL_OPT_2 = "--" + OPT_2;
  private static final List<String> OPTIONS = Arrays.asList(FULL_OPT_1, FULL_OPT_2);

  private UserOutput userOutput;
  private VetAdvancedCommandArgParser parser;
  private AutocompleteCommandFactory factory;

  @Before
  public void before() {
    userOutput = mock(UserOutput.class);

    parser = mock(VetAdvancedCommandArgParser.class);
    when(parser.getCommandArgName()).thenReturn(COMMAND);
    when(parser.getMatchingOptions(any())).thenReturn(OPTIONS);

    factory = new AutocompleteCommand.Factory(userOutput, Collections.singletonList(parser));
  }

  private AutocompleteCommand build(String... typedWords) {
    return factory.build(typedWords, typedWords.length - 1);
  }

  @Test
  public void WHEN_autocomplete_command_THEN_it_should_offer_opt_1_and_2() {
    build("vet", COMMAND, "").execute();
    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

    verify(userOutput).display(argumentCaptor.capture());
    String output = argumentCaptor.getValue();
    assertThat(output).isEqualTo(FULL_OPT_1 + "\n" + FULL_OPT_2);
  }

  @Test
  public void GIVEN_entered_opt1_WHEN_autocomplete_the_second_opt_THEN_it_should_offer_opt_2() {
    build("vet", COMMAND, FULL_OPT_1, "").execute();
    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

    verify(userOutput).display(argumentCaptor.capture());
    String output = argumentCaptor.getValue();
    assertThat(output).isEqualTo(FULL_OPT_2);
  }
}
