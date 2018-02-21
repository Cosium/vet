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

  private final PushUrl pushUrl;

  private final ChangeChangeId changeId;
  private final BranchShortName branch;
  private final ChangeSubject subject;

  GerritChange(PushUrl pushUrl, ChangeInfo changeInfo) {
    this(
        pushUrl,
        ChangeChangeId.of(changeInfo.changeId),
        BranchShortName.of(changeInfo.branch),
        ChangeSubject.of(changeInfo.subject));
  }

  private GerritChange(
      PushUrl pushUrl, ChangeChangeId changeId, BranchShortName branch, ChangeSubject subject) {
    requireNonNull(pushUrl);
    requireNonNull(changeId);
    requireNonNull(branch);
    requireNonNull(subject);

    this.pushUrl = pushUrl;
    this.changeId = changeId;
    this.branch = branch;
    this.subject = subject;
  }

  public ChangeChangeId getChangeId() {
    return changeId;
  }

  public ChangeSubject getSubject() {
    return subject;
  }

  public BranchShortName getBranch() {
    return branch;
  }

  public PushUrl getPushUrl() {
    return pushUrl;
  }
}
