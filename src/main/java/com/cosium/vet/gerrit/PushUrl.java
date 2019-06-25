package com.cosium.vet.gerrit;

import com.cosium.vet.utils.NonBlankString;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class PushUrl extends NonBlankString {

  private static final String SSH_SCHEMA = "ssh";
  private static final String HTTPS_SCHEMA = "https";

  private static final Pattern URL_PATTERN = Pattern.compile("(.*?)://(.*?)(:(.*?)|)/(.*?)/?$");
  private static final int SCHEMA_GROUP = 1;
  private static final int HOSTNAME_GROUP = 2;
  private static final int PROJECT_NAME_GROUP = 5;

  private PushUrl(String value) {
    super(value);
  }

  public static PushUrl of(String value) {
    return new PushUrl(value);
  }

  public ProjectName parseProjectName() {
    return ProjectName.of(buildValidMatcher().group(PROJECT_NAME_GROUP));
  }

  public String computeChangeWebUrl(ChangeNumericId numericId) {
    Matcher matcher = buildValidMatcher();
    String schema = matcher.group(SCHEMA_GROUP);
    if (SSH_SCHEMA.equals(schema)) {
      schema = HTTPS_SCHEMA;
    }
    return schema
        + "://"
        + matcher.group(HOSTNAME_GROUP)
        + "/c/"
        + matcher.group(PROJECT_NAME_GROUP)
        + "/+/"
        + numericId;
  }

  private Matcher buildValidMatcher() {
    Matcher matcher = URL_PATTERN.matcher(toString());
    if (!matcher.find()) {
      throw new RuntimeException("Could not parse the project name from '" + this + "'");
    }
    return matcher;
  }
}
