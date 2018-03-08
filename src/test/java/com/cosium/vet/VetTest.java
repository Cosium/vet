package com.cosium.vet;

import com.cosium.vet.command.DebugOptions;
import com.cosium.vet.gerrit.PatchSetSubject;
import com.cosium.vet.runtime.CommandRunner;
import com.cosium.vet.thirdparty.apache_commons_io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created on 22/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class VetTest extends GerritEnvironmentTest {

  private Path downstreamGitDir;
  private CommandRunner runner;
  private Vet tested;

  @Before
  public void before() throws Exception {
    Path workDir = Files.createTempDirectory("vet_");
    downstreamGitDir = workDir.resolve(PROJECT);
    runner = new TestCommandRunner();
    runner.run(
        workDir,
        "git",
        "clone",
        "http://" + USER + ":" + PASSWORD + "@" + gerritHost + ":" + gerritPort + "/" + PROJECT);
    runner.run(downstreamGitDir, "git", "config", "user.email", "you@example.com");
    runner.run(downstreamGitDir, "git", "config", "user.name", "Your Name");
    runner.run(downstreamGitDir, "git", "checkout", "-b", "feature");
    tested = new Vet(downstreamGitDir, runner, false, DebugOptions.empty());
  }

  @Test
  public void testPush() throws Exception {
    addAndCommitFile("bar", "The topic");
    tested.push(null, true, true, PatchSetSubject.of("Add bar"));

    addAndCommitFile("baz", "Random message");
    tested.push(null, null, null, PatchSetSubject.of("Add baz"));
  }

  // TODO Add test for fetching commit message edition patchset

  // TODO Add test for checking commit message stability

  private void addAndCommitFile(String fileName, String message) throws Exception {
    Path file = downstreamGitDir.resolve(fileName + ".txt");
    Files.createFile(file);
    try (OutputStream outputStream = Files.newOutputStream(file)) {
      IOUtils.write("I am a " + fileName + " !", outputStream, "UTF-8");
    }
    runner.run(downstreamGitDir, "git", "add", ".");
    runner.run(downstreamGitDir, "git", "commit", "-am", message);
  }
}
