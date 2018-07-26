package com.cosium.vet.gerrit;

import com.cosium.vet.utils.NonBlankString;

/**
 * Created on 23/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class PatchsetSubject extends NonBlankString {
  private PatchsetSubject(String value) {
    super(value);
  }

  public static PatchsetSubject of(String value) {
    return new PatchsetSubject(value);
  }
}
