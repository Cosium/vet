package com.cosium.vet.git;

import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;
import com.cosium.vet.utils.NonBlankString;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created on 27/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class CommitMessage extends NonBlankString {
  private CommitMessage(String value) {
    super(value);
  }

  public static CommitMessage of(String value) {
    return new CommitMessage(value);
  }

  public String removeLinesStartingWith(String... prefixes) {
    String filtered =
        Arrays.stream(toString().split("\n"))
            .filter(
                line -> {
                  if (StringUtils.isBlank(line)) {
                    return true;
                  }
                  for (String prefix : prefixes) {
                    if (line.startsWith(prefix)) {
                      return false;
                    }
                  }
                  return true;
                })
            .collect(Collectors.joining("\n"));
    return StringUtils.stripEnd(filtered, "\n");
  }
}
