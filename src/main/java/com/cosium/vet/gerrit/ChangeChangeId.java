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

  static ProjectBuilder builder() {
    return new ProjectBuilder();
  }

  // Builders

  static class ProjectBuilder {
    private GerritProjectName project;

    SourceBranchBuilder project(GerritProjectName project) {
      requireNonNull(project);
      this.project = project;
      return new SourceBranchBuilder(this);
    }
  }

  static class SourceBranchBuilder {
    private final ProjectBuilder projectBuilder;
    private BranchShortName sourceBranch;

    SourceBranchBuilder(ProjectBuilder projectBuilder) {
      requireNonNull(projectBuilder);
      this.projectBuilder = projectBuilder;
    }

    TargetBranchBuilder sourceBranch(BranchShortName featureBranch) {
      requireNonNull(featureBranch);
      this.sourceBranch = featureBranch;
      return new TargetBranchBuilder(this);
    }
  }

  static class TargetBranchBuilder {
    private final SourceBranchBuilder sourceBranchBuilder;
    private BranchShortName targetBranch;

    TargetBranchBuilder(SourceBranchBuilder sourceBranchBuilder) {
      requireNonNull(sourceBranchBuilder);
      this.sourceBranchBuilder = sourceBranchBuilder;
    }

    FinalBuilder targetBranch(BranchShortName targetBranch) {
      requireNonNull(targetBranch);
      this.targetBranch = targetBranch;
      return new FinalBuilder(this);
    }
  }

  static class FinalBuilder {
    private final TargetBranchBuilder targetBranchBuilder;

    FinalBuilder(TargetBranchBuilder targetBranchBuilder) {
      requireNonNull(targetBranchBuilder);
      this.targetBranchBuilder = targetBranchBuilder;
    }

    ChangeChangeId build() {
      String checksum =
          DigestUtils.shaHex(
              String.format(
                  "%s:%s->%s",
                  targetBranchBuilder.sourceBranchBuilder.projectBuilder.project,
                  targetBranchBuilder.sourceBranchBuilder.sourceBranch,
                  targetBranchBuilder.targetBranch));
      return new ChangeChangeId(String.format("I%s", checksum));
    }
  }
}
