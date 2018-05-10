package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.utils.NonBlankString;

/**
 * Created on 10/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class ChangeCheckoutBranchName extends NonBlankString {

  private ChangeCheckoutBranchName(String value) {
    super(value);
  }

  public static ChangeCheckoutBranchName of(String value) {
    return new ChangeCheckoutBranchName(value);
  }

  public static ChangeCheckoutBranchName defaults(ChangeNumericId numericId) {
    return ChangeCheckoutBranchName.of("change/" + numericId);
  }

  public BranchShortName toBranchShortName() {
    return BranchShortName.of(toString());
  }
}
