package com.cosium.vet.gerrit;

import com.cosium.vet.NonBlankStringValueObject;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class PushUrl extends NonBlankStringValueObject {
  private PushUrl(String value) {
    super(value);
  }

  public static PushUrl of(String value) {
    return new PushUrl(value);
  }
}
