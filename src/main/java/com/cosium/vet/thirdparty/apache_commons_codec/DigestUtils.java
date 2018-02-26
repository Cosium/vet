package com.cosium.vet.thirdparty.apache_commons_codec;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created on 25/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DigestUtils {

  public static String shaHex(String value) {
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
      return byteArrayToHexString(messageDigest.digest(value.getBytes()));
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  private static String byteArrayToHexString(byte[] bytes) {
    StringBuilder result = new StringBuilder();
    for (byte b : bytes) {
      result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
    }
    return result.toString();
  }
}
