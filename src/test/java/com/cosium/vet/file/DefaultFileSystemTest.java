package com.cosium.vet.file;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 20/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultFileSystemTest {

  private Path homePath;
  private DefaultFileSystem tested;

  @Before
  public void before() throws Exception {
    homePath = Files.createTempDirectory("vet_");
    tested = new DefaultFileSystem(homePath);
  }

  @Test
  public void WHEN_new_inputstream_inexisting_file_THEN_it_should_not_fail() throws Exception {
    tested.newAppFileInputStream(Files.createTempDirectory("vet_").resolve("t")).close();
  }

  @Test
  public void WHEN_new_outputstream_inexisting_file_THEN_it_should_not_fail() throws Exception {
    tested.newAppFileOutputStream(Files.createTempDirectory("vet_").resolve("t")).close();
  }

  @Test
  public void
      GIVEN_written_file_containing_hello_world_WHEN_reading_it_THEN_it_should_read_hello_world()
          throws Exception {
    Path file = Files.createTempDirectory("vet_").resolve("t");
    try (OutputStream outputStream = tested.newAppFileOutputStream(file)) {
      IOUtils.write("Hello World", outputStream, "UTF-8");
    }

    try (InputStream inputStream = tested.newAppFileInputStream(file)) {
      String content = IOUtils.toString(inputStream, "UTF-8");
      assertThat(content).isEqualTo("Hello World");
    }
  }

  @Test
  public void
      GIVEN_filesystem_with_home_dir_foo_WHEN_writing_new_file_THEN_it_should_be_written_in_foo_vet()
          throws Exception {
    try (OutputStream outputStream = tested.newAppFileOutputStream(Paths.get("t"))) {
      IOUtils.write("Hello World", outputStream, "UTF-8");
    }

    assertThat(Files.exists(homePath.resolve(".vet").resolve("t"))).isTrue();
  }
}
