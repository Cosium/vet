package com.cosium.vet.push;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class RemoteName {

  static final RemoteName ORIGIN = RemoteName.of("origin");

  private final String value;

  private RemoteName(String value) {
    if (StringUtils.isBlank(value)) {
      throw new IllegalArgumentException("Remote name value can't be null !");
    }
    this.value = value;
  }

  public static RemoteName of(String value) {
    return new RemoteName(value);
  }

  String value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RemoteName that = (RemoteName) o;
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
