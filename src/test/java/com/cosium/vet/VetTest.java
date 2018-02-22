package com.cosium.vet;

import com.cosium.vet.file.DefaultFileSystem;
import com.cosium.vet.gerrit.ChangeSubject;
import com.cosium.vet.gerrit.GerritPassword;
import com.cosium.vet.gerrit.GerritUser;
import com.cosium.vet.runtime.CommandRunner;
import com.cosium.vet.runtime.NonInteractiveUserInput;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created on 22/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class VetTest extends GerritEnvironmentTest {

  private CommandRunner runner;
  private Vet vet;

  @Before
  public void before() throws Exception {
    Path workDir = Files.createTempDirectory("vet_");
    Path downstreamGitDir = workDir.resolve(PROJECT);
    runner = new TestCommandRunner();
    runner.run(workDir, "git", "clone", "http://" + gerritHost + ":" + gerritPort + "/" + PROJECT);
    vet =
        new Vet(
            downstreamGitDir,
            new NonInteractiveUserInput(),
            runner,
            new DefaultFileSystem(workDir));
  }

  @Test
  public void testFirstPush() {
    vet.push(
        GerritUser.of(USER), GerritPassword.of(PASSWORD), null, ChangeSubject.of("Hello world"));
  }
}
