package com.cosium.vet.gerrit;

import com.cosium.vet.utils.NonBlankString;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class ProjectName extends NonBlankString {
  private ProjectName(String value) {
    super(value);
  }

  public static ProjectName of(String value) {
    return new ProjectName(value);
  }
}
