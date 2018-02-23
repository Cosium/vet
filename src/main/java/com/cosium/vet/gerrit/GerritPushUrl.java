package com.cosium.vet.gerrit;

import com.cosium.vet.utils.NonBlankString;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class GerritPushUrl extends NonBlankString {

  private GerritPushUrl(String value) {
    super(value);
  }

  public static GerritPushUrl of(String value) {
    return new GerritPushUrl(value);
  }

  public GerritProjectName parseProjectName() {
    Pattern pattern = Pattern.compile("[^/]+(?=/$|$)");
    Matcher matcher = pattern.matcher(toString());
    if (!matcher.find()) {
      throw new RuntimeException("WTF?");
    }
    return GerritProjectName.of(matcher.group(0));
  }
}
