package com.cosium.vet.git;

import com.cosium.vet.NonBlankStringValueObject;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class BranchShortName extends NonBlankStringValueObject {

  public static final BranchShortName MASTER = BranchShortName.of("master");

  private BranchShortName(String value) {
    super(value);
  }

  public static BranchShortName of(String value) {
    return new BranchShortName(value);
  }
}
