package com.cosium.vet.git;

import com.cosium.vet.runtime.ProcessBuilderFactory;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.BranchConfig;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class DefaultGitRepository implements GitRepository {

  private final Repository repository;
  private final ProcessBuilderFactory processBuilderFactory;

  DefaultGitRepository(Repository repository, ProcessBuilderFactory processBuilderFactory) {
    requireNonNull(repository);
    requireNonNull(processBuilderFactory);
    this.repository = repository;
    this.processBuilderFactory = processBuilderFactory;
  }

  @Override
  public String getBranchRemote() {
    return Optional.ofNullable(getBranchConfig().getRemote())
        .orElseThrow(() -> new RuntimeException("No 'remote' found for branch " + getBranch()));
  }

  @Override
  public String getBranchMerge() {
    return Optional.ofNullable(getBranchConfig().getMerge())
        .orElseThrow(() -> new RuntimeException("No 'merge' found for branch " + getBranch()));
  }

  @Override
  public String getMostRecentCommonCommit(String otherBranch) {
    try (RevWalk walk = new RevWalk(repository)) {
      RevCommit currentBranchHead = walk.parseCommit(repository.resolve(Constants.HEAD));
      walk.reset();
      RevCommit otherBranchHead = walk.parseCommit(repository.resolve(otherBranch));
      walk.reset();

      walk.setRevFilter(RevFilter.MERGE_BASE);
      walk.markStart(currentBranchHead);
      walk.markStart(otherBranchHead);
      return Optional.ofNullable(walk.next())
          .map(RevObject::getId)
          .map(AnyObjectId::getName)
          .orElseThrow(
              () ->
                  new RuntimeException(
                      "Could not compute the merge base between "
                          + Constants.HEAD
                          + " and "
                          + otherBranch));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getTree() {
    try (RevWalk revWalk = new RevWalk(repository)) {
      RevCommit commit = revWalk.parseCommit(repository.resolve(Constants.HEAD));
      return commit.getTree().getName();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String commitTree(String tree, String parent, String commitMessage) {
    try {
      ProcessBuilder processBuilder =
          processBuilderFactory.create(
              "git", "commit-tree", tree, "-p", parent, "-m", commitMessage);
      processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);
      processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

      Process process = processBuilder.start();
      int exitCode = process.waitFor();
      if (exitCode != 0) {
        throw new RuntimeException(
            String.format("'%s' failed with code %s", "git-commit-tree", exitCode));
      }

      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        return reader.readLine();
      }
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private BranchConfig getBranchConfig() {
    return new BranchConfig(repository.getConfig(), getBranch());
  }

  private String getBranch() {
    try {
      return repository.getBranch();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
