package com.cosium.vet.gerrit;

import com.cosium.vet.utils.NonBlankString;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 10/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class CodeReviewVote extends NonBlankString {

  private static final Pattern VALIDATION_PATTERN = Pattern.compile("[+|\\-]\\d");

  private CodeReviewVote(String value) {
    super(value);
    Matcher matcher = VALIDATION_PATTERN.matcher(value);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("'" + value + "' is not a valid code review vote format");
    }
  }

  public static CodeReviewVote of(String value) {
    return new CodeReviewVote(value);
  }
}
