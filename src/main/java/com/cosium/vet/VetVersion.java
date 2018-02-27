package com.cosium.vet;

import com.cosium.vet.thirdparty.apache_commons_io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created on 27/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class VetVersion {

  public static final String VALUE;

  static {
    VALUE = loadVersion();
  }

  private static String loadVersion() {
    InputStream inputStream = VetVersion.class.getResourceAsStream("version.txt");
    if (inputStream == null) {
      return "0.0";
    }
    try {
      return IOUtils.toString(inputStream, "UTF-8");
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        inputStream.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
