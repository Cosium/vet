package com.cosium.vet;

/**
 * Created on 24/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class UserUtils {

  static String getCurrentUser() {
    return System.getProperty("user.name");
  }
}
