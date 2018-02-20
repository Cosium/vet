package com.cosium.vet.git;

import com.cosium.vet.TestCommandRunner;
import com.cosium.vet.runtime.CommandRunner;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class GitConfigRepositoryTest {

  private CommandRunner runner;
  private Path repo;

  private GitConfigRepository tested;

  @Before
  public void before() throws Exception {
    Path workDir = Files.createTempDirectory("vet");

    Path remoteRepo = workDir.resolve("upstream");
    Files.createDirectories(remoteRepo);

    runner = new TestCommandRunner();
    runner.run(remoteRepo, "git", "init");
    runner.run(remoteRepo, "git", "config", "user.email", "\"you@example.com\"");
    runner.run(remoteRepo, "git", "config", "user.name", "\"Your Name\"");
    Files.createFile(remoteRepo.resolve("foo.txt"));
    runner.run(remoteRepo, "git", "add", ".");
    runner.run(remoteRepo, "git", "commit", "-am", "\"Initial commit\"");

    runner.run(workDir, "git", "clone", "./upstream", "downstream-tmp");
    repo = workDir.resolve("downstream");
    FileUtils.copyDirectory(workDir.resolve("downstream-tmp").toFile(), repo.toFile());
    runner.run(repo, "git", "config", "user.email", "\"you@example.com\"");
    runner.run(repo, "git", "config", "user.name", "\"Your Name\"");

    GitProvider gitProvider = new GitProvider(repo, runner);
    tested = gitProvider.buildRepository();
  }

  @Test
  public void GIVEN_key_remote_to_origin_WHEN_getting_remote_THEN_it_should_return_origin() {
    assertThat(tested.getCurrentBranchValue("remote")).isEqualTo("origin");
  }

  @Test
  public void GIVEN_absent_key_foo_WHEN_getting_foo_THEN_it_should_return_null() {
    assertThat(tested.getCurrentBranchValue("foo")).isNull();
  }

  @Test
  public void GIVEN_set_key_foo_to_bar_WHEN_getting_foo_THEN_it_should_return_bar() {
    tested.setCurrentBranchValue("foo", "bar");
    assertThat(tested.getCurrentBranchValue("foo")).isEqualTo("bar");
  }
}
