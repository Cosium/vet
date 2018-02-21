package com.cosium.vet.git;

import com.cosium.vet.NonBlankStringValueObject;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class RemoteUrl extends NonBlankStringValueObject {
  private RemoteUrl(String value) {
    super(value);
  }

  public static RemoteUrl of(String value) {
    return new RemoteUrl(value);
  }
}
