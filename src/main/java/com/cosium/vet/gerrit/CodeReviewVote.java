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

  public static final CodeReviewVote PLUS_2 = CodeReviewVote.of("+2");

  private CodeReviewVote(String value) {
    super(value);
    Matcher matcher = Pattern.compile("[+|\\-]\\d").matcher(value);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("'" + value + "' is not a valid code review vote format");
    }
  }

  public static CodeReviewVote of(String value) {
    return new CodeReviewVote(value);
  }
}
