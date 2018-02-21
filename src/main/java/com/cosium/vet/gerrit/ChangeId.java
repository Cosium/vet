package com.cosium.vet.gerrit;

import com.cosium.vet.NonBlankStringValueObject;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class ChangeId extends NonBlankStringValueObject {

  private ChangeId(String value) {
    super(value);
  }

  public static ChangeId of(String value) {
    return new ChangeId(value);
  }
}
