package com.cosium.vet.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static java.util.Objects.requireNonNull;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultFileSystem implements FileSystem {

  private final Path appDir;

  public DefaultFileSystem() {
    this(Paths.get(System.getProperty("user.home")));
  }

  public DefaultFileSystem(Path homeDir) {
    requireNonNull(homeDir);
    this.appDir = homeDir.resolve(".vet");
    try {
      Files.createDirectories(appDir);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public InputStream newAppFileInputStream(Path file) {
    try {
      Path fullPath = appDir.resolve(file);
      if (!Files.exists(fullPath)) {
        Files.createFile(fullPath);
      }
      return Files.newInputStream(fullPath, StandardOpenOption.READ);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public OutputStream newAppFileOutputStream(Path file) {
    try {
      Path fullPath = appDir.resolve(file);
      if (!Files.exists(fullPath)) {
        Files.createFile(fullPath);
      }
      return Files.newOutputStream(
          fullPath, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
