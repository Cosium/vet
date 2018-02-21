package com.cosium.vet.git;

import com.cosium.vet.TestCommandRunner;
import com.cosium.vet.runtime.CommandRunner;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class GitTestRepository {

  private static final Logger LOG = LoggerFactory.getLogger(GitTestRepository.class);

  final Path repo;
  final CommandRunner runner;

  private GitTestRepository(Path repo, CommandRunner runner) {
    requireNonNull(repo);
    requireNonNull(runner);
    this.repo = repo;
    this.runner = runner;
  }

  static Builder builder() {
    return new Builder();
  }

  static class Builder {

    private Builder() {}

    GitTestRepository build() throws Exception {
      Path workDir = Files.createTempDirectory("vet");

      Path remoteRepo = workDir.resolve("upstream");
      Files.createDirectories(remoteRepo);

      CommandRunner runner = new TestCommandRunner();

      runner.run(remoteRepo, "git", "init");
      runner.run(remoteRepo, "git", "config", "user.email", "\"you@example.com\"");
      runner.run(remoteRepo, "git", "config", "user.name", "\"Your Name\"");
      Files.createFile(remoteRepo.resolve("foo.txt"));
      runner.run(remoteRepo, "git", "add", ".");
      runner.run(remoteRepo, "git", "commit", "-am", "\"Initial commit\"");

      runner.run(workDir, "git", "clone", "./upstream", "downstream-tmp");
      Path repo = workDir.resolve("downstream");
      FileUtils.copyDirectory(workDir.resolve("downstream-tmp").toFile(), repo.toFile());
      runner.run(repo, "git", "config", "user.email", "\"you@example.com\"");
      runner.run(repo, "git", "config", "user.name", "\"Your Name\"");



      return new GitTestRepository(repo, runner);
    }
  }
}
