package com.cosium.vet.git;

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



  private CommandRunner commandRunner = new BasicCommandRunner();
  private GitExecutor git = new DockerGitExecutor(commandRunner);

  private Path repo;
  private GitClient tested;

  @Before
  public void before() throws Exception {
    Path workDir = Files.createTempDirectory("vet");

    Path remoteRepo = workDir.resolve("upstream");
    Files.createDirectories(remoteRepo);
    git.execute(remoteRepo, "init");
    git.execute(remoteRepo, "config", "user.email", "\"you@example.com\"");
    git.execute(remoteRepo, "config", "user.name", "\"Your Name\"");
    Files.createFile(remoteRepo.resolve("foo.txt"));
    git.execute(remoteRepo, "add", ".");
    git.execute(remoteRepo, "commit", "-am", "\"Initial commit\"");

    git.execute(workDir, "clone", "./upstream", "downstream-tmp");
    repo = workDir.resolve("downstream");
    FileUtils.copyDirectory(workDir.resolve("downstream-tmp").toFile(), repo.toFile());
    git.execute(repo, "config", "user.email", "\"you@example.com\"");
    git.execute(repo, "config", "user.name", "\"Your Name\"");

    tested = new DefaultGitClient(repo, git);
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
    String expectedCommit = git.execute(repo, "rev-parse", "HEAD");
    git.execute(repo, "checkout", "-b", "getMostRecentCommonCommit");
    Path barPath = repo.resolve("bar.txt");
    Files.createFile(barPath);
    git.execute(repo, "add", ".");
    git.execute(repo, "commit", "-am", "\"Add bar\"");

    assertThat(tested.getMostRecentCommonCommit("origin/master")).isEqualTo(expectedCommit);

    git.execute(repo, "checkout", expectedCommit);
    assertThat(Files.exists(barPath)).isFalse();
  }

  @Test
  public void testCommitTree() throws Exception {
    Path barPath = repo.resolve("bar.txt");
    Files.createFile(barPath);
    git.execute(repo, "add", ".");
    git.execute(repo, "commit", "-am", "\"Add bar\"");

    Path bazPath = repo.resolve("baz.txt");
    Files.createFile(bazPath);
    git.execute(repo, "add", ".");
    git.execute(repo, "commit", "-am", "\"Add baz\"");

    String commit =
        tested.commitTree(
            tested.getTree(),
            tested.getMostRecentCommonCommit("origin/master"),
            "Create a new commit tree");

    assertThat(commit).isNotBlank();

    git.execute(repo, "checkout", commit);
    assertThat(Files.exists(barPath)).isTrue();
    assertThat(Files.exists(bazPath)).isTrue();
  }
}
