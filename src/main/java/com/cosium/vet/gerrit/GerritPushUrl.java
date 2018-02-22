package com.cosium.vet.gerrit;

import com.cosium.vet.utils.Url;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class GerritPushUrl extends Url {

  private GerritPushUrl(String value) {
    super(value, false);
  }

  public static GerritPushUrl of(String value) {
    return new GerritPushUrl(value);
  }

  public GerritHttpRootUrl parseHttpRootUrl() {
    Pattern pattern = Pattern.compile("(.*)/.+/?$");
    Matcher matcher = pattern.matcher(toString());
    if (!matcher.find()) {
      throw new RuntimeException("WTF?");
    }
    return GerritHttpRootUrl.of(matcher.group(1));
  }

  public GerritProjectName parseProjectName() {
    Pattern pattern = Pattern.compile("[^/]+(?=/$|$)");
    Matcher matcher = pattern.matcher(toString());
    if (!matcher.find()) {
      throw new RuntimeException("WTF?");
    }
    return GerritProjectName.of(matcher.group(0));
  }

  public GerritPushUrl withCredentials(GerritCredentials credentials) {
    String protocol = protocol();
    String currentUrl = toString();

    return GerritPushUrl.of(
        String.format(
            "%s://%s:%s@%s",
            protocol,
            credentials.getUser(),
            credentials.getPassword(),
            StringUtils.substring(
                currentUrl, protocol.length() + "://".length(), currentUrl.length())));
  }
}
