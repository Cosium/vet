package com.cosium.vet.gerrit.config;

import com.cosium.vet.file.FileSystem;
import com.cosium.vet.gerrit.ChangeId;
import com.cosium.vet.gerrit.GerritHttpRootUrl;
import com.cosium.vet.git.GitConfigRepository;
import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONObjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class MixedGerritConfigurationRepository implements GerritConfigurationRepository {

  private static final Logger LOG =
      LoggerFactory.getLogger(MixedGerritConfigurationRepository.class);

  private static final String VET_CHANGE_ID = "vet-change-id";

  private final FileSystem fileSystem;
  private final GitConfigRepository gitConfigRepository;
  private final Path configFile;

  MixedGerritConfigurationRepository(
      FileSystem fileSystem, GitConfigRepository gitConfigRepository) {
    requireNonNull(fileSystem);
    requireNonNull(gitConfigRepository);
    this.fileSystem = fileSystem;
    this.gitConfigRepository = gitConfigRepository;
    this.configFile = Paths.get("gerrit-config.json");
  }

  @Override
  public GerritConfiguration read() {
    FileStoredConfig fileStoredConf;
    try (InputStream inputStream = fileSystem.newAppFileInputStream(configFile)) {
      fileStoredConf = JSON.std.beanFrom(FileStoredConfig.class, inputStream);
    } catch (JSONObjectException e) {
      LOG.debug(
          "{} does not exist or has a wrongly formatted content. Returning empty configuration.",
          configFile);
      fileStoredConf = new FileStoredConfig();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return new MixedConfig(
        fileStoredConf,
        new GitStoredConfig(gitConfigRepository.getCurrentBranchValue(VET_CHANGE_ID)));
  }

  @Override
  public <T> T readAndWrite(Function<GerritConfiguration, T> functor) {
    GerritConfiguration gerritConfiguration = read();
    T result = functor.apply(gerritConfiguration);
    write(gerritConfiguration);
    return result;
  }

  private void write(GerritConfiguration config) {
    if (!(config instanceof MixedConfig)) {
      throw new RuntimeException(
          String.format("Configuration %s was not built by repository %s", config, this));
    }

    MixedConfig mixedConf = (MixedConfig) config;
    try (OutputStream outputStream = fileSystem.newAppFileOutputStream(configFile)) {
      JSON.std.with(JSON.Feature.PRETTY_PRINT_OUTPUT).write(mixedConf.fileStored, outputStream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    gitConfigRepository.setCurrentBranchValue(VET_CHANGE_ID, mixedConf.gitStored.changeId);
  }

  /**
   * Created on 19/02/18.
   *
   * @author Reda.Housni-Alaoui
   */
  private static class SiteAuthConfig implements GerritSiteAuthConfiguration {

    private String httpUrl;
    private String httpLogin;
    private String httpPassword;

    @Override
    public String getHttpUrl() {
      return httpUrl;
    }

    @Override
    public void setHttpUrl(String httpUrl) {
      this.httpUrl = httpUrl;
    }

    @Override
    public String getHttpLogin() {
      return httpLogin;
    }

    @Override
    public void setHttpLogin(String httpLogin) {
      this.httpLogin = httpLogin;
    }

    @Override
    public String getHttpPassword() {
      return httpPassword;
    }

    @Override
    public void setHttpPassword(String httpPassword) {
      this.httpPassword = httpPassword;
    }
  }

  /**
   * Created on 19/02/18.
   *
   * @author Reda.Housni-Alaoui
   */
  private static class FileStoredConfig {

    /** Site by http url */
    private Map<String, SiteAuthConfig> sites;

    FileStoredConfig() {
      this.sites = Collections.emptyMap();
    }

    Map<String, SiteAuthConfig> getSites() {
      return sites;
    }

    void setSites(Map<String, SiteAuthConfig> sites) {
      if (sites == null) {
        this.sites = Collections.emptyMap();
      } else {
        this.sites = Collections.unmodifiableMap(sites);
      }
    }
  }

  /**
   * Created on 19/02/18.
   *
   * @author Reda.Housni-Alaoui
   */
  private class GitStoredConfig {

    private String changeId;

    private GitStoredConfig(String changeId) {
      this.changeId = changeId;
    }
  }

  /**
   * Created on 19/02/18.
   *
   * @author Reda.Housni-Alaoui
   */
  private class MixedConfig implements GerritConfiguration {

    private final FileStoredConfig fileStored;
    private final GitStoredConfig gitStored;

    MixedConfig(FileStoredConfig fileStored, GitStoredConfig gitStored) {
      requireNonNull(fileStored);
      requireNonNull(gitStored);
      this.fileStored = fileStored;
      this.gitStored = gitStored;
    }

    @Override
    public Optional<ChangeId> getChangeId() {
      return Optional.ofNullable(gitStored.changeId).map(ChangeId::of);
    }

    @Override
    public void setChangeId(ChangeId changeId) {
      if (changeId == null) {
        gitStored.changeId = null;
      } else {
        gitStored.changeId = changeId.toString();
      }
    }

    @Override
    public GerritSiteAuthConfiguration setAndGetSiteAuth(
        GerritHttpRootUrl httpUrl, String httpLogin, String httpPassword) {
      Map<String, SiteAuthConfig> modifiableMap = new HashMap<>(fileStored.getSites());
      SiteAuthConfig newConf = new SiteAuthConfig();
      newConf.setHttpUrl(httpUrl.toString());
      newConf.setHttpLogin(httpLogin);
      newConf.setHttpPassword(httpPassword);
      modifiableMap.put(httpUrl.toString(), newConf);
      fileStored.setSites(modifiableMap);
      return newConf;
    }

    @Override
    public Optional<GerritSiteAuthConfiguration> getSiteAuth(GerritHttpRootUrl httpUrl) {
      return Optional.ofNullable(fileStored.getSites().get(httpUrl.toString()));
    }

    @Override
    public void dropSiteAuth(GerritHttpRootUrl httpUrl) {
      Map<String, SiteAuthConfig> modifiableMap = new HashMap<>(fileStored.getSites());
      modifiableMap.remove(httpUrl.toString());
      fileStored.setSites(modifiableMap);
    }
  }
}
