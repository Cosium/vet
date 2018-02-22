package com.cosium.vet;

import com.cosium.vet.gerrit.ChangeSubject;
import com.cosium.vet.gerrit.GerritPassword;
import com.cosium.vet.gerrit.GerritUser;
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
public class VetTest extends TestWithGerrit {

  private Vet vet;

  @Before
  public void before() throws Exception {
    Path workDir = Files.createTempDirectory("vet");
    Path downstreamGitDir = workDir.resolve(PROJECT);
    RUNNER.run(
        workDir,
        "git",
        "clone",
        "http://" + USER + ":" + PASSWORD + "@" + host + ":" + port + "/" + PROJECT);
    vet = new Vet(downstreamGitDir, new NonInteractiveUserInput(), RUNNER);
  }

  @Test
  public void testFirstPush() {
    vet.push(
        GerritUser.of(USER), GerritPassword.of(PASSWORD), ChangeSubject.of("Hello world"), null);
  }
}
