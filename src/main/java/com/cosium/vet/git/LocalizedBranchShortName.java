package com.cosium.vet.git;

import com.cosium.vet.utils.NonBlankString;
import org.apache.commons.lang3.StringUtils;

import static java.util.Objects.requireNonNull;

/**
 * Created on 23/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class LocalizedBranchShortName extends NonBlankString {
  private LocalizedBranchShortName(String value) {
    super(value);
  }

  public static LocalizedBranchShortName of(String value) {
    return new LocalizedBranchShortName(value);
  }

  public static LocalizedBranchShortName of(BranchShortName branchShortName) {
    return of(null, branchShortName);
  }

  public static LocalizedBranchShortName of(
      RemoteName remoteName, BranchShortName branchShortName) {
    requireNonNull(branchShortName);
    return new LocalizedBranchShortName(
        String.format(
            "%s%s",
            remoteName != null ? String.format("%s/", remoteName) : StringUtils.EMPTY,
            branchShortName));
  }
}
