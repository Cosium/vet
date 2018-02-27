package com.cosium.vet.gerrit;

import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;
import com.cosium.vet.thirdparty.apache_commons_codec.DigestUtils;
import com.cosium.vet.utils.NonBlankString;

import static java.util.Objects.requireNonNull;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class ChangeChangeId extends NonBlankString {

  private ChangeChangeId(String value) {
    super(value);
  }

  static class Factory implements ChangeChangeIdFactory {
    private static final Logger LOG = LoggerFactory.getLogger(Factory.class);

    private final GerritProjectName project;

    Factory(GerritProjectName project) {
      requireNonNull(project);
      this.project = project;
    }

    @Override
    public ChangeChangeId build(BranchShortName sourceBranch, BranchShortName targetBranch) {
      String checksum =
          DigestUtils.shaHex(String.format("%s:%s->%s", project, sourceBranch, targetBranch));

      ChangeChangeId changeChangeId = new ChangeChangeId(String.format("I%s", checksum));
      LOG.debug(
          "Built ChangeChangeId '{}' for project '{}', source branch '{}', and target branch '{}'",
          changeChangeId,
          project,
          sourceBranch,
          targetBranch);
      return changeChangeId;
    }
  }
}
