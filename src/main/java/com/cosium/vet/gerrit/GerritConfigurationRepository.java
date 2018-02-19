package com.cosium.vet.gerrit;

import com.cosium.vet.file.FileSystem;
import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONObjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.util.Objects.requireNonNull;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class GerritConfigurationRepository {

  private static final Logger LOG = LoggerFactory.getLogger(GerritConfigurationRepository.class);

  private final FileSystem fileSystem;
  private final Path configurationFile;

  GerritConfigurationRepository(FileSystem fileSystem) {
    requireNonNull(fileSystem);
    this.fileSystem = fileSystem;
    this.configurationFile = Paths.get("gerrit-config.json");
  }

  GerritConfiguration read() {
    try (InputStream inputStream = fileSystem.newAppFileInputStream(configurationFile)) {
      return JSON.std.beanFrom(GerritConfiguration.class, inputStream);
    } catch (JSONObjectException e) {
      LOG.debug(
          "{} does not exist or has a wrongly formatted content. Returning empty configuration.",
          configurationFile);
      return new GerritConfiguration();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  void write(GerritConfiguration gerritConfiguration) {
    try (OutputStream outputStream = fileSystem.newAppFileOutputStream(configurationFile)) {
      JSON.std.with(JSON.Feature.PRETTY_PRINT_OUTPUT).write(gerritConfiguration, outputStream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
