package com.cosium.vet.gerrit;

import com.cosium.vet.utils.NonBlankString;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class ChangeId extends NonBlankString {

  private ChangeId(String value) {
    super(value);
  }

  /** @param value project~issuenumber (i.e. foo~1234) */
  public static ChangeId of(String value) {
    return new ChangeId(value);
  }
}
