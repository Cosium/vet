package com.cosium.vet.git;

import com.cosium.vet.utils.NonBlankString;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class RemoteName extends NonBlankString {

  public static final RemoteName ORIGIN = RemoteName.of("origin");

  private RemoteName(String value) {
    super(value);
  }

  public static RemoteName of(String value) {
    return new RemoteName(value);
  }

  public BranchShortName branch(BranchShortName localBranchName) {
    return BranchShortName.of(toString() + "/" + localBranchName.toString());
  }
}
