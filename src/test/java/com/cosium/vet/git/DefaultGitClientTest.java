package com.cosium.vet.git;

import com.cosium.vet.TestCommandRunner;
import com.cosium.vet.runtime.BasicCommandRunner;
import com.cosium.vet.runtime.CommandRunner;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 17/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultGitClientTest {

  private CommandRunner runner = new TestCommandRunner(new BasicCommandRunner());

  private Path repo;
  private GitClient tested;

  @Before
  public void before() throws Exception {
    Path workDir = Files.createTempDirectory("vet");

    Path remoteRepo = workDir.resolve("upstream");
    Files.createDirectories(remoteRepo);
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

    tested = new DefaultGitClient(repo, runner);
  }

  @Test
  public void testGetBranchRemote() {
    assertThat(tested.getBranchRemote()).isEqualTo("origin");
  }

  @Test
  public void testGetBranchMerge() {
    assertThat(tested.getBranchMerge()).isEqualTo("refs/heads/master");
  }

  @Test
  public void testGetTree() {
    assertThat(tested.getTree()).isNotBlank();
  }

  @Test
  public void getMostRecentCommonCommit() throws Exception {
    String expectedCommit = runner.run(repo,"git", "rev-parse", "HEAD");
    runner.run(repo, "git","checkout", "-b", "getMostRecentCommonCommit");
    Path barPath = repo.resolve("bar.txt");
    Files.createFile(barPath);
    runner.run(repo,"git", "add", ".");
    runner.run(repo,"git", "commit", "-am", "\"Add bar\"");

    assertThat(tested.getMostRecentCommonCommit("origin/master")).isEqualTo(expectedCommit);

    runner.run(repo,"git", "checkout", expectedCommit);
    assertThat(Files.exists(barPath)).isFalse();
  }

  @Test
  public void testCommitTree() throws Exception {
    Path barPath = repo.resolve("bar.txt");
    Files.createFile(barPath);
    runner.run(repo,"git", "add", ".");
    runner.run(repo,"git", "commit", "-am", "\"Add bar\"");

    Path bazPath = repo.resolve("baz.txt");
    Files.createFile(bazPath);
    runner.run(repo,"git", "add", ".");
    runner.run(repo,"git", "commit", "-am", "\"Add baz\"");

    String commit =
        tested.commitTree(
            tested.getTree(),
            tested.getMostRecentCommonCommit("origin/master"),
            "Create a new commit tree");

    assertThat(commit).isNotBlank();

    runner.run(repo,"git", "checkout", commit);
    assertThat(Files.exists(barPath)).isTrue();
    assertThat(Files.exists(bazPath)).isTrue();
  }
}
