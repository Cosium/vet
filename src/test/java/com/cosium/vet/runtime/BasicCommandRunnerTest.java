package com.cosium.vet.runtime;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

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
    String output = tested.run(directory, Environments.empty(), "docker", "run", "hello-world");
    assertThat(output).contains("Hello");
  }

  @Test
  public void testEnvironmentVariable() {
    try {
      tested.run(directory, Environments.empty(), "/bin/bash", "-c", "echo");
    } catch (Exception e) {
      Assume.assumeNoException(e);
    }

    assertThat(
            tested.run(
                directory,
                new StubbedEnvironment(),
                "/bin/bash",
                "-c",
                "echo $" + StubbedEnvironment.ENV_VARIABLE))
        .isEqualTo(StubbedEnvironment.ENV_VALUE);
  }

  private static class StubbedEnvironment implements Environment {

    private static final String ENV_VARIABLE = "foo";
    private static final String ENV_VALUE = "bar";

    @Override
    public Map<String, String> asMap() {
      return Collections.singletonMap(ENV_VARIABLE, ENV_VALUE);
    }
  }
}
