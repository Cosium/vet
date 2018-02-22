package com.cosium.vet;

import com.cosium.vet.runtime.CommandRunner;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created on 22/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public abstract class TestWithGerrit {

  private static final String GERRIT_NAME = "gerrit_1";
  private static final int GERRIT_INTERNAL_PORT = 8080;
  protected static final String USER = "fry";
  protected static final String PASSWORD = "fry";

  private static final Path GERRIT_DIR;
  protected static final String PROJECT = "foo";

  protected static final CommandRunner RUNNER = new TestCommandRunner();

  static {
    try {
      GERRIT_DIR = Files.createTempDirectory("vet-gerrit");
      FileUtils.copyDirectory(new File("src/test/resources/gerrit"), GERRIT_DIR.toFile());

      Path gerritProjectGitDir = GERRIT_DIR.resolve("git").resolve(PROJECT);
      Files.createDirectories(gerritProjectGitDir);

      RUNNER.run(gerritProjectGitDir, "git", "init");

      RUNNER.run(gerritProjectGitDir, "git", "init");
      RUNNER.run(gerritProjectGitDir, "git", "config", "user.email", "\"you@example.com\"");
      RUNNER.run(gerritProjectGitDir, "git", "config", "user.name", "\"Your Name\"");
      Files.createFile(gerritProjectGitDir.resolve("foo.txt"));
      RUNNER.run(gerritProjectGitDir, "git", "add", ".");
      RUNNER.run(gerritProjectGitDir, "git", "commit", "-am", "\"Initial commit\"");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @ClassRule
  public static final DockerComposeContainer DOCKER_ENV =
      new DockerComposeContainer(GERRIT_DIR.resolve("docker-compose.yml").toFile())
          .withExposedService(GERRIT_NAME, GERRIT_INTERNAL_PORT);

  protected static String host;
  protected static int port;

  @BeforeClass
  public static void beforeClass() {
    host = DOCKER_ENV.getServiceHost(GERRIT_NAME, GERRIT_INTERNAL_PORT);
    port = DOCKER_ENV.getServicePort(GERRIT_NAME, GERRIT_INTERNAL_PORT);
  }
}
