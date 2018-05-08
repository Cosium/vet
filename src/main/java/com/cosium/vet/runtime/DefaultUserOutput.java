package com.cosium.vet.runtime;

import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;

/**
 * Created on 23/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultUserOutput implements UserOutput {
  @Override
  public void display(String message) {
    System.out.print(message + StringUtils.SPACE);
  }
}
