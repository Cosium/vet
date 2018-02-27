package com.cosium.vet.git;

import com.cosium.vet.utils.NonBlankString;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created on 27/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class CommitMessage extends NonBlankString {
  private CommitMessage(String value) {
    super(value);
  }

  public static CommitMessage of(String value) {
    return new CommitMessage(value);
  }

  public CommitMessage removeLinesContaining(String content) {
    return new CommitMessage(
        Arrays.stream(toString().split("\n"))
            .filter(s -> !s.contains(content))
            .collect(Collectors.joining("\n")));
  }
}
