package com.cosium.vet.git;

import com.cosium.vet.utils.NonBlankString;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class RemoteUrl extends NonBlankString {

  private RemoteUrl(String value) {
    super(value);
  }

  public static RemoteUrl of(String url) {
    return new RemoteUrl(url);
  }
}
