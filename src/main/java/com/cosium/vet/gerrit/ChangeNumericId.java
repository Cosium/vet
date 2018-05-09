package com.cosium.vet.gerrit;

import java.util.Objects;

/**
 * Created on 06/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class ChangeNumericId {

  public static final ChangeNumericId NONE = ChangeNumericId.of(-1);

  private final long value;

  private ChangeNumericId(long value) {
    this.value = value;
  }

  public static ChangeNumericId of(long value) {
    return new ChangeNumericId(value);
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangeNumericId that = (ChangeNumericId) o;
    return value == that.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
