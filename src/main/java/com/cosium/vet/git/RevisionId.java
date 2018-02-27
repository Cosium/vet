package com.cosium.vet.git;

import com.cosium.vet.utils.NonBlankString;

/**
 * Created on 27/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class RevisionId extends NonBlankString {
  private RevisionId(String value) {
    super(value);
  }

  /** i.e. 96a9475f9992e5324afc5a5020a456da0dcc1c4f */
  public static RevisionId of(String value) {
    return new RevisionId(value);
  }
}
