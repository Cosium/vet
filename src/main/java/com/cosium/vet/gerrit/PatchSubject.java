package com.cosium.vet.gerrit;

import com.cosium.vet.utils.NonBlankString;

/**
 * Created on 23/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class PatchSubject extends NonBlankString {
  private PatchSubject(String value) {
    super(value);
  }

  public static PatchSubject of(String value) {
    return new PatchSubject(value);
  }
}
