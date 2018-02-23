package com.cosium.vet.gerrit;

import com.cosium.vet.utils.NonBlankString;

/**
 * Created on 23/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class PatchSetSubject extends NonBlankString {
  private PatchSetSubject(String value) {
    super(value);
  }

  public static PatchSetSubject of(String value) {
    return new PatchSetSubject(value);
  }
}
