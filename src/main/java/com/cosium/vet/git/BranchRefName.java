package com.cosium.vet.git;

import com.cosium.vet.utils.NonBlankString;

/**
 * Created on 27/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class BranchRefName extends NonBlankString {
  private BranchRefName(String value) {
    super(value);
  }

  /** i.e. refs/heads/master */
  public static BranchRefName of(String value) {
    return new BranchRefName(value);
  }
}
