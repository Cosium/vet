package com.cosium.vet.git;

import com.cosium.vet.runtime.BasicCommandRunner;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 17/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DockerGitExecutorTest {

  private DockerGitExecutor tested = new DockerGitExecutor(new BasicCommandRunner());

  @Test
  public void testVersion() throws Exception {
    String version = tested.execute(Files.createTempDirectory(null), "--version");
    assertThat(version).contains("2.13.0");
  }
}
