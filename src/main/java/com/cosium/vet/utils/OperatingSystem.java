package com.cosium.vet.utils;

import com.cosium.vet.thirdparty.apache_commons_lang3.SystemUtils;

/**
 * Created on 30/06/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class OperatingSystem {

  public boolean isWindows() {
    return SystemUtils.IS_OS_WINDOWS;
  }
}
