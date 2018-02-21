package com.cosium.vet.gerrit;

import com.cosium.vet.utils.Url;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class GerritHttpRootUrl extends Url {

  private GerritHttpRootUrl(String value) {
    super(value, true);
  }

  public static GerritHttpRootUrl of(String value) {
    return new GerritHttpRootUrl(value);
  }
}
