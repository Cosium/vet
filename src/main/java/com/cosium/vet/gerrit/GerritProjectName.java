package com.cosium.vet.gerrit;

import com.cosium.vet.utils.NonBlankString;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class GerritProjectName extends NonBlankString {
  private GerritProjectName(String value) {
    super(value);
  }

  public static GerritProjectName of(String value) {
    return new GerritProjectName(value);
  }
}
