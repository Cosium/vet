package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchShortName;
import com.google.gerrit.extensions.common.ChangeInfo;

import static java.util.Objects.requireNonNull;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class GerritChange {

  private final GerritPushUrl pushUrl;

  private final ChangeChangeId changeId;
  private final BranchShortName branch;
  private final ChangeSubject subject;

  GerritChange(GerritPushUrl pushUrl, ChangeInfo changeInfo) {
    this(
        pushUrl,
        ChangeChangeId.of(changeInfo.changeId),
        BranchShortName.of(changeInfo.branch),
        ChangeSubject.of(changeInfo.subject));
  }

  private GerritChange(
      GerritPushUrl pushUrl,
      ChangeChangeId changeId,
      BranchShortName branch,
      ChangeSubject subject) {
    requireNonNull(pushUrl);
    requireNonNull(changeId);
    requireNonNull(branch);
    requireNonNull(subject);

    this.pushUrl = pushUrl;
    this.changeId = changeId;
    this.branch = branch;
    this.subject = subject;
  }

  ChangeChangeId getChangeId() {
    return changeId;
  }

  ChangeSubject getSubject() {
    return subject;
  }

  public BranchShortName getBranch() {
    return branch;
  }

  GerritPushUrl getPushUrl() {
    return pushUrl;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("GerritChange{");
    sb.append("pushUrl=").append(pushUrl);
    sb.append(", changeId=").append(changeId);
    sb.append(", branch=").append(branch);
    sb.append(", subject=").append(subject);
    sb.append('}');
    return sb.toString();
  }
}
