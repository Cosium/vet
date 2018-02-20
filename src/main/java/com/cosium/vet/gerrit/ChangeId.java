package com.cosium.vet.gerrit;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class ChangeId {

  private final String value;

  private ChangeId(String value) {
    if (StringUtils.isBlank(value)) {
      throw new IllegalArgumentException("ChangeId value can't be blank");
    }
    this.value = value;
  }

  static ChangeId of(String value) {
    return new ChangeId(value);
  }

  String value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangeId changeId = (ChangeId) o;
    return Objects.equals(value, changeId.value);
  }

  @Override
  public int hashCode() {

    return Objects.hash(value);
  }
}
