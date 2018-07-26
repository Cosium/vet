package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchRefName;
import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 06/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class ChangeNumericId {

  private final long value;

  private ChangeNumericId(long value) {
    this.value = value;
  }

  public static ChangeNumericId of(long value) {
    return new ChangeNumericId(value);
  }

  public static ChangeNumericId parseFromPushToRefForOutput(
      PushUrl pushUrl, String pushToRefForOutput) {
    ProjectName projectName = pushUrl.parseProjectName();
    Pattern pattern = Pattern.compile(Pattern.quote(projectName.toString()) + ".\\S*?(\\d+)");
    Matcher matcher = pattern.matcher(pushToRefForOutput);
    if (!matcher.find()) {
      throw new RuntimeException(
          "Could not parse change numeric id from output '" + pushToRefForOutput + "'");
    }
    return ChangeNumericId.of(Long.parseLong(matcher.group(1)));
  }

  public BranchRefName branchRefName(Patchset patchset) {
    String numericIdStr = toString();
    String numericIdSuffix = StringUtils.substring(numericIdStr, numericIdStr.length() - 2);
    return BranchRefName.of(
        "refs/changes/" + numericIdSuffix + "/" + numericIdStr + "/" + patchset.getNumber());
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangeNumericId that = (ChangeNumericId) o;
    return value == that.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
