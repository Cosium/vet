package com.cosium.vet.gerrit;

import com.cosium.vet.utils.NonBlankString;

/**
 * Created on 22/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class GerritPassword extends NonBlankString {
  private GerritPassword(String value) {
    super(value);
  }

  public static GerritPassword of(String value) {
    return new GerritPassword(value);
  }
}
