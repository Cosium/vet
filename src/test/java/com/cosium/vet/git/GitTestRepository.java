package com.cosium.vet.git;

import com.cosium.vet.TestCommandRunner;
import com.cosium.vet.runtime.CommandRunner;
import com.cosium.vet.runtime.Environments;

import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class GitTestRepository {

  public final Path repo;
  public final CommandRunner runner;

  private GitTestRepository(Path repo, CommandRunner runner) {
    requireNonNull(repo);
    requireNonNull(runner);
    this.repo = repo;
    this.runner = runner;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private Builder() {}

    public GitTestRepository build() throws Exception {
      Path workDir = Files.createTempDirectory("vet_");

      Path remoteRepo = workDir.resolve("upstream");
      Files.createDirectories(remoteRepo);

      CommandRunner runner = new TestCommandRunner();

      runner.run(remoteRepo, Environments.empty(), "git", "init");
      runner.run(
          remoteRepo, Environments.empty(), "git", "config", "user.email", "you@example.com");
      runner.run(remoteRepo, Environments.empty(), "git", "config", "user.name", "Your Name");
      Files.createFile(remoteRepo.resolve("foo.txt"));
      runner.run(remoteRepo, Environments.empty(), "git", "add", ".");
      runner.run(remoteRepo, Environments.empty(), "git", "commit", "-am", "Initial commit");

      runner.run(workDir, Environments.empty(), "git", "clone", "./upstream", "downstream");
      Path repo = workDir.resolve("downstream");
      runner.run(repo, Environments.empty(), "git", "config", "user.email", "you@example.com");
      runner.run(repo, Environments.empty(), "git", "config", "user.name", "Your Name");

      return new GitTestRepository(repo, runner);
    }
  }
}
