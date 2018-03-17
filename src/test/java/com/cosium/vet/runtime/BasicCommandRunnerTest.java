package com.cosium.vet.runtime;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 17/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class BasicCommandRunnerTest {

  private Path directory;
  private BasicCommandRunner tested;

  @Before
  public void before() throws Exception {
    directory = Files.createTempDirectory("vet_");
    tested = new BasicCommandRunner();
  }

  @Test
  public void WHEN_run_docker_hello_world_THEN_it_should_print_at_least_hello() {
    String output = tested.run(directory, "docker", "run", "hello-world");
    assertThat(output).contains("Hello");
  }
}
