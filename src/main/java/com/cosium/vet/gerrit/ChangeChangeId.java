package com.cosium.vet.gerrit;

import com.cosium.vet.NonBlankStringValueObject;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class ChangeChangeId extends NonBlankStringValueObject {
  private ChangeChangeId(String value) {
    super(value);
  }

  public static ChangeChangeId of(String value) {
    return new ChangeChangeId(value);
  }
}
