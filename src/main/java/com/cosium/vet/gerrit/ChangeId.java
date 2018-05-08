package com.cosium.vet.gerrit;

import com.cosium.vet.utils.NonBlankString;

/**
 * Identifier that uniquely identifies one change. It contains the URL-encoded project name as well
 * as the change number: "'<project>~<numericId>'"
 *
 * <p>Created on 06/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class ChangeId extends NonBlankString {

  private ChangeId(String value) {
    super(value);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private GerritProjectName projectName;
    private ChangeNumericId numericId;

    private Builder() {}

    public Builder projectName(GerritProjectName projectName) {
      this.projectName = projectName;
      return this;
    }

    public Builder numericId(ChangeNumericId numericId) {
      this.numericId = numericId;
      return this;
    }

    public ChangeId build() {
      return new ChangeId(projectName + "~" + numericId);
    }
  }
}
