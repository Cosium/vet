package com.cosium.vet;

import com.cosium.vet.runtime.CommandRunner;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created on 22/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public abstract class GerritEnvironmentTest {

  private static final Logger LOG = LoggerFactory.getLogger(GerritEnvironmentTest.class);

  private static final String GERRIT_NAME = "gerrit_1";
  private static final int GERRIT_INTERNAL_PORT = 8080;
  protected static final String USER = "fry";
  protected static final String PASSWORD = "fry";
  protected static final String PROJECT = "foo";

  private static DockerComposeContainer gerritRunner;
  protected static String host;
  protected static int port;

  @BeforeClass
  public static void beforeClass() throws Exception {
    Path gerritDir = Files.createTempDirectory("vet-gerrit_");
    FileUtils.copyDirectory(new File("src/test/resources/gerrit"), gerritDir.toFile());

    Files.createDirectories(gerritDir.resolve("git"));
    Files.createDirectories(gerritDir.resolve("cache"));
    Files.createDirectories(gerritDir.resolve("index"));
    Files.createDirectories(gerritDir.resolve("plugins"));
    Files.createDirectories(gerritDir.resolve("logs"));
    Files.createDirectories(gerritDir.resolve("db"));

    CommandRunner runner = new TestCommandRunner();

    LOG.info("Initializing Gerrit");
    long initStart = System.currentTimeMillis();
    runner.run(
        gerritDir,
        "docker-compose",
        "-f",
        "run-gerrit.yml",
        "-f",
        "init-gerrit.yml",
        "up",
        "--abort-on-container-exit");
    LOG.info("Gerrit initialized in {}ms", System.currentTimeMillis() - initStart);

    Path gerritProjectGitDir = gerritDir.resolve("git").resolve(PROJECT);
    Files.createDirectories(gerritProjectGitDir);

    runner.run(gerritProjectGitDir, "git", "init");
    runner.run(gerritProjectGitDir, "git", "init");
    runner.run(gerritProjectGitDir, "git", "config", "user.email", "\"you@example.com\"");
    runner.run(gerritProjectGitDir, "git", "config", "user.name", "\"Your Name\"");
    Files.createFile(gerritProjectGitDir.resolve("foo.txt"));
    runner.run(gerritProjectGitDir, "git", "add", ".");
    runner.run(gerritProjectGitDir, "git", "commit", "-am", "\"Initial commit\"");

    LOG.info("Running Gerrit");
    gerritRunner =
        new DockerComposeContainer(gerritDir.resolve("run-gerrit.yml").toFile())
            .withExposedService(GERRIT_NAME, GERRIT_INTERNAL_PORT);
    gerritRunner.starting(Description.EMPTY);

    host = gerritRunner.getServiceHost(GERRIT_NAME, GERRIT_INTERNAL_PORT);
    port = gerritRunner.getServicePort(GERRIT_NAME, GERRIT_INTERNAL_PORT);
  }

  @AfterClass
  public static void afterClass() {
    LOG.info("Stopping Gerrit");
    gerritRunner.finished(Description.EMPTY);
  }
}
