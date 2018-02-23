package com.cosium.vet;

import com.cosium.vet.gerrit.GerritPassword;
import com.cosium.vet.gerrit.GerritUser;
import com.cosium.vet.gerrit.PatchSetSubject;
import com.cosium.vet.runtime.CommandRunner;
import com.cosium.vet.runtime.NonInteractiveUserInput;
import org.apache.commons.io.IOUtils;
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
    tested = new Vet(downstreamGitDir, new NonInteractiveUserInput(), runner);
  }

  @Test
  public void testFirstPush() throws Exception {
    addAndCommitFile("bar", "Hello world\n\nWhat's up !");
    tested.push(
        GerritUser.of(USER), GerritPassword.of(PASSWORD), null, PatchSetSubject.of("Add bar"));

    addAndCommitFile("baz", "Hello world\n\nWhat's up !");
    tested.push(
        GerritUser.of(USER), GerritPassword.of(PASSWORD), null, PatchSetSubject.of("Add baz"));
  }

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
