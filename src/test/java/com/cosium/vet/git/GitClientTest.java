package com.cosium.vet.git;

import com.cosium.vet.runtime.CommandRunner;
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
public class GitClientTest {

  private CommandRunner runner;
  private Path repo;

  private GitClient tested;

  @Before
  public void before() throws Exception {
    GitTestRepository testRepository = GitTestRepository.builder().build();
    runner = testRepository.runner;
    repo = testRepository.repo;

    GitProvider gitProvider = new GitProvider(repo, runner);
    tested = gitProvider.build();
  }

  @Test
  public void testGetBranch() {
    assertThat(tested.getBranch()).isEqualTo(BranchShortName.MASTER);
  }

  @Test
  public void testGetTree() {
    assertThat(tested.getTree()).isNotBlank();
  }

  @Test
  public void getMostRecentCommonCommit() throws Exception {
    String expectedCommit = runner.run(repo, "git", "rev-parse", "HEAD");
    runner.run(repo, "git", "checkout", "-b", "getMostRecentCommonCommit");
    Path barPath = repo.resolve("bar.txt");
    Files.createFile(barPath);
    runner.run(repo, "git", "add", ".");
    runner.run(repo, "git", "commit", "-am", "Add bar");

    assertThat(tested.getMostRecentCommonCommit("origin/master")).isEqualTo(expectedCommit);

    runner.run(repo, "git", "checkout", expectedCommit);
    assertThat(Files.exists(barPath)).isFalse();
  }

  @Test
  public void testCommitTree() throws Exception {
    Path barPath = repo.resolve("bar.txt");
    Files.createFile(barPath);
    runner.run(repo, "git", "add", ".");
    runner.run(repo, "git", "commit", "-am", "Add bar");

    Path bazPath = repo.resolve("baz.txt");
    Files.createFile(bazPath);
    runner.run(repo, "git", "add", ".");
    runner.run(repo, "git", "commit", "-am", "Add baz");

    String commit =
        tested.commitTree(
            tested.getTree(),
            tested.getMostRecentCommonCommit("origin/master"),
            "Create a new commit tree");

    assertThat(commit).isNotBlank();

    runner.run(repo, "git", "checkout", commit);
    assertThat(Files.exists(barPath)).isTrue();
    assertThat(Files.exists(bazPath)).isTrue();
  }

  @Test
  public void testGetLastCommitMessage() throws Exception {
    Path barPath = repo.resolve("bar.txt");
    Files.createFile(barPath);
    runner.run(repo, "git", "add", ".");
    runner.run(repo, "git", "commit", "-am", "Add bar");

    assertThat(tested.getLastCommitMessage()).isEqualTo("Add bar");
  }
}
