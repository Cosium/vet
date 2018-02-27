package com.cosium.vet.utils;


import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;

import java.util.Objects;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public abstract class NonBlankString {

  private final String value;

  protected NonBlankString(String value) {
    if (StringUtils.isBlank(value)) {
      throw new IllegalArgumentException(getClass().getSimpleName() + " can't be blank");
    }
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    NonBlankString that = (NonBlankString) o;
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
