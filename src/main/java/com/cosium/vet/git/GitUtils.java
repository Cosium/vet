package com.cosium.vet.git;

import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class GitUtils {

  public static String encodeForGitRef(String text) {
    String safe = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789- ";
    StringBuilder encoded = new StringBuilder();
    for (char c : text.toCharArray()) {
      if (safe.indexOf(c) != -1) {
        encoded.append(c);
      } else {
        encoded.append(String.format("%%%02X", (int) c));
      }
    }
    return StringUtils.replaceAll(encoded.toString(), StringUtils.SPACE, "_");
  }
}
