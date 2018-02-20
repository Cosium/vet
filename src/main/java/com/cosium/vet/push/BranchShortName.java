package com.cosium.vet.push;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class BranchShortName {

  private final String value;

  static final BranchShortName MASTER = BranchShortName.of("master");

  private BranchShortName(String value) {
    if (StringUtils.isBlank(value)) {
      throw new IllegalArgumentException("Branch name can't be null");
    }
    this.value = value;
  }

  public static BranchShortName of(String value) {
    return new BranchShortName(value);
  }

  String value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BranchShortName that = (BranchShortName) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return value;
  }
}
