package com.cosium.vet.gerrit;

import com.cosium.vet.utils.NonBlankString;

/**
 * Created on 22/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class GerritUser extends NonBlankString {
  private GerritUser(String value) {
    super(value);
  }

  public static GerritUser of(String value) {
    return new GerritUser(value);
  }
}
