package com.cosium.vet.push;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class ChangeDescription {

  private final String value;

  private ChangeDescription(String value) {
    if (StringUtils.isBlank(value)) {
      throw new IllegalArgumentException("Change description can't be blank");
    }
    this.value = value;
  }

  public static ChangeDescription of(String value) {
    return new ChangeDescription(value);
  }

  String value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangeDescription that = (ChangeDescription) o;
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
