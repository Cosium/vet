package com.cosium.vet.runtime;

import org.junit.Test;

import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 17/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class BasicCommandRunnerTest {

  private final BasicCommandRunner tested = new BasicCommandRunner();

  @Test
  public void testHelloWorld() throws Exception {
    String output = tested.run(Files.createTempDirectory(null), "docker", "run", "hello-world");
    assertThat(output).contains("Hello");
  }
}
