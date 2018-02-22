package com.cosium.vet.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public abstract class Url {

  private final URL url;

  protected Url(String value, boolean enforceHttp) {
    try {
      this.url = new URL(value);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(e);
    }
    if (enforceHttp && !url.getProtocol().matches("https?")) {
      throw new IllegalArgumentException(
          String.format(
              "'%s' passed to %s is using protocol '%s'. The protocol must be 'http' or 'https'.",
              url, getClass().getSimpleName(), url.getProtocol()));
    }
  }

  protected String protocol() {
    return url.getProtocol();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Url url1 = (Url) o;
    return Objects.equals(url, url1.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url);
  }

  @Override
  public String toString() {
    return url.toString();
  }
}
