package com.cosium.vet.git;

import com.cosium.vet.utils.NonBlankString;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class BranchShortName extends NonBlankString {

  public static final BranchShortName MASTER = BranchShortName.of("master");

  private BranchShortName(String value) {
    super(value);
  }

  public static BranchShortName of(String value) {
    return new BranchShortName(value);
  }
}
