package com.cosium.vet.command.autocomplete;

/**
 * Created on 11/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface AutocompleteCommandFactory {

  /**
   * @param typedWordArray The array of typed words
   * @param highlightedWordIndex The index of the highlighted word
   * @return A new command
   */
  AutocompleteCommand build(String[] typedWordArray, Integer highlightedWordIndex);
}
