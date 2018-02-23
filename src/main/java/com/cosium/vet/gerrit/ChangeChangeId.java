package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.utils.NonBlankString;
import org.apache.commons.codec.digest.DigestUtils;

import static java.util.Objects.requireNonNull;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class ChangeChangeId extends NonBlankString {

  private ChangeChangeId(String value) {
    super(value);
  }

  static class Factory implements ChangeChangeIdFactory {
    private final GerritProjectName project;

    Factory(GerritProjectName project) {
      requireNonNull(project);
      this.project = project;
    }

    @Override
    public ChangeChangeId build(BranchShortName sourceBranch, BranchShortName targetBranch) {
      String checksum =
          DigestUtils.shaHex(String.format("%s:%s->%s", project, sourceBranch, targetBranch));
      return new ChangeChangeId(String.format("I%s", checksum));
    }
  }
}
