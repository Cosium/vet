package com.cosium.vet.gerrit;

import com.cosium.vet.NonBlankStringValueObject;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class ChangeSubject extends NonBlankStringValueObject {

  private ChangeSubject(String value) {
    super(value);
  }

  public static ChangeSubject of(String value) {
    return new ChangeSubject(value);
  }
}
