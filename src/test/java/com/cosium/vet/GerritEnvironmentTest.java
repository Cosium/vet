package com.cosium.vet;

import com.cosium.vet.runtime.CommandRunner;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

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
  private static String gerritRootHttpUrl;

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

  private static void setFullPermissions(Path dir) throws IOException {
    Files.setPosixFilePermissions(dir, Set.of(PosixFilePermission.values()));
  }

  private static void createDirs(Path dir) throws IOException {
    Files.createDirectories(dir);
    setFullPermissions(dir);
  }

  @BeforeClass
  public static void beforeClass() throws Exception {
    Path gerritDir = Files.createTempDirectory("vet-gerrit_");
    setFullPermissions(gerritDir);
    FileUtils.copyDirectory(new File("src/test/resources/gerrit"), gerritDir.toFile());

    gerritHost = "localhost";
    try (ServerSocket serverSocket = new ServerSocket(0)) {
      gerritPort = serverSocket.getLocalPort();
    }
    gerritRootHttpUrl = "http://" + gerritHost + ":" + gerritPort + "/";

    writePort(gerritDir.resolve(RUN_YML));
    writePort(gerritDir.resolve("etc").resolve("gerrit.config"));

    createDirs(gerritDir.resolve("git"));
    createDirs(gerritDir.resolve("cache"));
    createDirs(gerritDir.resolve("index"));
    createDirs(gerritDir.resolve("plugins"));
    createDirs(gerritDir.resolve("logs"));
    createDirs(gerritDir.resolve("db"));
    setFullPermissions(gerritDir.resolve("etc"));

    CommandRunner runner = new TestCommandRunner();

    LOG.info("Current user is {}", UserUtils.getCurrentUser());
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
    createDirs(gerritProjectGitDir);

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

    CloseableHttpClient client = HttpClientBuilder.create().build();
    HttpUriRequest request =
        RequestBuilder.post(gerritRootHttpUrl + "login/")
            .setHeader("Content-Type", "application/x-www-form-urlencoded")
            .setEntity(
                new UrlEncodedFormEntity(
                    Lists.newArrayList(
                        new BasicNameValuePair("username", USER),
                        new BasicNameValuePair("password", PASSWORD))))
            .build();
    while (true) {
      try (CloseableHttpResponse response = client.execute(request)) {
        assertThat(response.getStatusLine().getStatusCode()).isLessThan(400);
        break;
      } catch (Throwable e) {
        LOG.info("Waiting on {}", gerritRootHttpUrl);
        Thread.sleep(1000);
      }
    }

    TailerListener listener = new GerritServerListener();
    Tailer tailer =
        new Tailer(gerritDir.resolve("logs").resolve("log.log").toFile(), listener, 200);
    Thread thread = new Thread(tailer);
    thread.setDaemon(true);
    thread.start();
  }

  @AfterClass
  public static void afterClass() {
    LOG.info("Stopping Gerrit");
    gerritRunner.finished(Description.EMPTY);
  }

  private static class GerritServerListener extends TailerListenerAdapter {
    public void handle(String line) {
      System.out.println("[GerritServer] " + line);
    }
  }
}
