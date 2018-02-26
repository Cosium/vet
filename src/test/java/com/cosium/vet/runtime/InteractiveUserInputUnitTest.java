package com.cosium.vet.runtime;

import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created on 23/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class InteractiveUserInputUnitTest {

  private static final String HOW_ARE_YOU = "How are you";
  private static final String FINE = "I am fine thank you";
  private static final String ILL = "I am ill";

  private InputScanner inputScanner;
  private UserOutput userOutput;
  private UserInput tested;

  @Before
  public void before() {
    inputScanner = mock(InputScanner.class);
    userOutput = mock(UserOutput.class);
    tested = new InteractiveUserInput(inputScanner, userOutput);
  }

  @Test
  public void GIVEN_input_blank_WHEN_asknonblank_with_default_FINE_THEN_return_FINE() {
    when(inputScanner.nextLine()).thenReturn(StringUtils.EMPTY);
    assertThat(tested.askNonBlank(HOW_ARE_YOU, FINE)).isEqualTo(FINE);
  }

  @Test
  public void GIVEN_input_ILL_WHEN_asknonblank_with_default_foo_THEN_return_ILL() {
    when(inputScanner.nextLine()).thenReturn(ILL);
    assertThat(tested.askNonBlank(HOW_ARE_YOU, FINE)).isEqualTo(ILL);
  }

  @Test
  public void
      WHEN_askNonBlank_HOW_ARE_YOU_with_default_FINE_THEN_displayHOW_ARE_YOU_default_FINE() {
    tested.askNonBlank(HOW_ARE_YOU, FINE);
    verify(userOutput).display(eq(HOW_ARE_YOU + " [" + FINE + "]:"));
  }

  @Test
  public void WHEN_askNonBlank_HOW_ARE_YOU_THEN_display_HOW_ARE_YOU() {
    when(inputScanner.nextLine()).thenReturn(ILL);
    tested.askNonBlank(HOW_ARE_YOU);
    verify(userOutput).display(eq(HOW_ARE_YOU + ":"));
  }

  @Test
  public void WHEN_ask_HOW_ARE_YOU_THEN_display_HOW_ARE_YOU() {
    tested.ask(HOW_ARE_YOU);
    verify(userOutput).display(eq(HOW_ARE_YOU + ":"));
  }

  @Test
  public void
      GIVEN_user_inputing_blank_then_ILL_WHEN_askNonBlank_HOW_ARE_YOU_THEN_it_should_return_ILL() {
    when(inputScanner.nextLine()).thenReturn(StringUtils.EMPTY, ILL);
    assertThat(tested.askNonBlank(HOW_ARE_YOU)).isEqualTo(ILL);
    verify(inputScanner, times(2)).nextLine();
  }

  @Test
  public void
      GIVEN_user_inputing_blank_then_ILL_WHEN_askNonBlank_HOW_ARE_YOU_THEN_default_FINE_it_should_return_FINE() {
    when(inputScanner.nextLine()).thenReturn(StringUtils.EMPTY, ILL);
    assertThat(tested.askNonBlank(HOW_ARE_YOU, FINE)).isEqualTo(FINE);
    verify(inputScanner).nextLine();
  }
}
