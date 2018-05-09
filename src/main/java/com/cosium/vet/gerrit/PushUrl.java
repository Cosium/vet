package com.cosium.vet.gerrit;

import com.cosium.vet.utils.NonBlankString;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class PushUrl extends NonBlankString {

  private PushUrl(String value) {
    super(value);
  }

  public static PushUrl of(String value) {
    return new PushUrl(value);
  }

  public ProjectName parseProjectName() {
    Pattern pattern = Pattern.compile("[^/]+(?=/$|$)");
    Matcher matcher = pattern.matcher(toString());
    if (!matcher.find()) {
      throw new RuntimeException("WTF?");
    }
    return ProjectName.of(matcher.group(0));
  }
}
