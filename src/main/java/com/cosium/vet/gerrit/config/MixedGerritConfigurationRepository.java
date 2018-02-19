package com.cosium.vet.gerrit.config;

import com.cosium.vet.file.FileSystem;
import com.cosium.vet.git.GitConfigRepository;
import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONObjectException;
import org.apache.commons.lang3.StringUtils;
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

import static java.util.Objects.requireNonNull;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class MixedGerritConfigurationRepository implements GerritConfigurationRepository {

  private static final Logger LOG =
      LoggerFactory.getLogger(MixedGerritConfigurationRepository.class);

  private static final String VET_CURRENT_ISSUE_ID = "vet-current-issue-id";
  private static final String VET_SELECTED_SITE_HTTP_URL = "vet-selected-site-http-url";

  private final FileSystem fileSystem;
  private final GitConfigRepository gitConfigRepository;
  private final Path authConfigFile;

  MixedGerritConfigurationRepository(
      FileSystem fileSystem, GitConfigRepository gitConfigRepository) {
    requireNonNull(fileSystem);
    requireNonNull(gitConfigRepository);
    this.fileSystem = fileSystem;
    this.gitConfigRepository = gitConfigRepository;
    this.authConfigFile = Paths.get("gerrit-config.json");
  }

  @Override
  public GerritConfiguration read() {
    FileStoredConfig fileStoredConf;
    try (InputStream inputStream = fileSystem.newAppFileInputStream(authConfigFile)) {
      fileStoredConf = JSON.std.beanFrom(FileStoredConfig.class, inputStream);
    } catch (JSONObjectException e) {
      LOG.debug(
          "{} does not exist or has a wrongly formatted content. Returning empty configuration.",
          authConfigFile);
      fileStoredConf = new FileStoredConfig();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return new MixedConfig(
        this,
        fileStoredConf,
        new GitStoredConfig(
            gitConfigRepository.getValue(VET_CURRENT_ISSUE_ID),
            gitConfigRepository.getValue(VET_SELECTED_SITE_HTTP_URL)));
  }

  @Override
  public void write(GerritConfiguration config) {
    if (!(config instanceof MixedConfig)) {
      throw new RuntimeException(
          String.format("Configuration %s was not built by repository %s", config, this));
    }

    MixedConfig mixedConf = (MixedConfig) config;
    if (!mixedConf.isOwner(this)) {
      throw new RuntimeException(
          String.format("Configuration %s was not built by repository %s", config, this));
    }

    try (OutputStream outputStream = fileSystem.newAppFileOutputStream(authConfigFile)) {
      JSON.std.with(JSON.Feature.PRETTY_PRINT_OUTPUT).write(mixedConf.fileStored, outputStream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    gitConfigRepository.setValue(VET_CURRENT_ISSUE_ID, mixedConf.gitStored.currentIssueId);
    gitConfigRepository.setValue(
        VET_SELECTED_SITE_HTTP_URL, mixedConf.gitStored.selectedSiteHttpUrl);
  }

  /**
   * Created on 19/02/18.
   *
   * @author Reda.Housni-Alaoui
   */
  private static class SiteConfig implements GerritSiteConfiguration {

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
    private Map<String, SiteConfig> sites;

    FileStoredConfig() {
      this.sites = Collections.emptyMap();
    }

    public Map<String, SiteConfig> getSites() {
      return sites;
    }

    public void setSites(Map<String, SiteConfig> sites) {
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

    private String currentIssueId;
    private String selectedSiteHttpUrl;

    private GitStoredConfig(String currentIssueId, String selectedSiteHttpUrl) {
      this.currentIssueId = currentIssueId;
      this.selectedSiteHttpUrl = selectedSiteHttpUrl;
    }
  }

  /**
   * Created on 19/02/18.
   *
   * @author Reda.Housni-Alaoui
   */
  private class MixedConfig implements GerritConfiguration {

    private final Object owner;
    private final FileStoredConfig fileStored;
    private final GitStoredConfig gitStored;

    MixedConfig(Object owner, FileStoredConfig fileStored, GitStoredConfig gitStored) {
      requireNonNull(owner);
      requireNonNull(fileStored);
      requireNonNull(gitStored);
      this.owner = owner;
      this.fileStored = fileStored;
      this.gitStored = gitStored;
    }

    boolean isOwner(Object potentialOwner) {
      return owner == potentialOwner;
    }

    @Override
    public Optional<String> getCurrentIssueId() {
      return Optional.ofNullable(gitStored.currentIssueId);
    }

    @Override
    public void setCurrentIssueId(String issueId) {
      gitStored.currentIssueId = StringUtils.defaultIfBlank(issueId, null);
    }

    @Override
    public Optional<GerritSiteConfiguration> getSelectedSite() {
      return Optional.ofNullable(gitStored.selectedSiteHttpUrl)
          .map(id -> fileStored.getSites().get(id));
    }

    @Override
    public void selectSite(String siteHttpUrl) {
      this.gitStored.selectedSiteHttpUrl = StringUtils.defaultIfBlank(siteHttpUrl, null);
    }

    @Override
    public void addSite(String httpUrl, String httpLogin, String httpPassword) {
      Map<String, SiteConfig> modifiableMap = new HashMap<>(fileStored.getSites());
      SiteConfig newConf = new SiteConfig();
      newConf.setHttpUrl(httpUrl);
      newConf.setHttpLogin(httpLogin);
      newConf.setHttpPassword(httpPassword);
      modifiableMap.put(httpUrl, newConf);
      fileStored.setSites(modifiableMap);
    }
  }
}
