package com.cosium.vet;

import com.cosium.vet.gerrit.GerritHttpRootUrl;
import com.cosium.vet.runtime.CommandRunner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * Created on 22/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public abstract class GerritEnvironmentTest {

  private static final Logger LOG = LoggerFactory.getLogger(GerritEnvironmentTest.class);
  private static final String RUN_YML = "run-gerrit.yml";

  protected static final String USER = "fry";
  protected static final String PASSWORD = "fry";
  protected static final String PROJECT = "foo";

  private static DockerComposeContainer gerritRunner;
  protected static String gerritHost;
  protected static int gerritPort;
  protected static GerritHttpRootUrl gerritRootHttpUrl;

  private static void writePort(Path file) throws Exception {
    try (InputStream inputStream = Files.newInputStream(file)) {
      String content = IOUtils.toString(inputStream, "UTF-8");
      try (OutputStream outputStream = Files.newOutputStream(file)) {
        IOUtils.write(
            StringUtils.replaceAll(
                content, Pattern.quote("${httpPort}"), String.valueOf(gerritPort)),
            outputStream,
            "UTF-8");
      }
    }
  }

  @BeforeClass
  public static void beforeClass() throws Exception {
    Path gerritDir = Files.createTempDirectory("vet-gerrit_");
    FileUtils.copyDirectory(new File("src/test/resources/gerrit"), gerritDir.toFile());

    gerritHost = "localhost";
    try (ServerSocket serverSocket = new ServerSocket(0)) {
      gerritPort = serverSocket.getLocalPort();
    }
    gerritRootHttpUrl = GerritHttpRootUrl.of("http://" + gerritHost + ":" + gerritPort + "/");

    writePort(gerritDir.resolve(RUN_YML));
    writePort(gerritDir.resolve("etc").resolve("gerrit.config"));

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
        RUN_YML,
        "-f",
        "init-gerrit.yml",
        "up",
        "--abort-on-container-exit");
    LOG.info("Gerrit initialized in {}ms", System.currentTimeMillis() - initStart);

    Path gerritProjectGitDir = gerritDir.resolve("git").resolve(PROJECT);
    Files.createDirectories(gerritProjectGitDir);

    runner.run(gerritProjectGitDir, "git", "init");
    runner.run(gerritProjectGitDir, "git", "config", "user.email", "you@example.com");
    runner.run(gerritProjectGitDir, "git", "config", "user.name", "Your Name");
    Files.createFile(gerritProjectGitDir.resolve("foo.txt"));
    runner.run(gerritProjectGitDir, "git", "add", ".");
    runner.run(gerritProjectGitDir, "git", "commit", "-am", "Initial commit");

    LOG.info("Starting Gerrit");
    gerritRunner = new DockerComposeContainer(gerritDir.resolve(RUN_YML).toFile());
    gerritRunner.starting(Description.EMPTY);

    LOG.info("Gerrit starting on {}", gerritRootHttpUrl);

    while (true) {
      CloseableHttpClient client = HttpClientBuilder.create().build();
      try (CloseableHttpResponse ignored =
          client.execute(new HttpGet(gerritRootHttpUrl.toString()))) {
        break;
      } catch (Throwable e) {
        Thread.sleep(1000);
      }
    }
  }

  @AfterClass
  public static void afterClass() {
    LOG.info("Stopping Gerrit");
    gerritRunner.finished(Description.EMPTY);
  }
}
