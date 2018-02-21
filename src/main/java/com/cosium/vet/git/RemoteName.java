package com.cosium.vet.git;

import com.cosium.vet.NonBlankStringValueObject;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class RemoteName extends NonBlankStringValueObject {

  public static final RemoteName ORIGIN = RemoteName.of("origin");

  private RemoteName(String value) {
    super(value);
  }

  public static RemoteName of(String value) {
    return new RemoteName(value);
  }
}
