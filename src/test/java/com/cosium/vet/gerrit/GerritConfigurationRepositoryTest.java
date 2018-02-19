package com.cosium.vet.gerrit;

import com.cosium.vet.file.DefaultFileSystem;
import com.cosium.vet.file.FileSystem;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class GerritConfigurationRepositoryTest {

  private static final String URL = "https://foo.com";
  private static final String LOGIN = "foo";
  private static final String PASSWORD = "bar";

  private GerritConfigurationRepository tested;

  @Before
  public void before() throws Exception {
    Path tempDir = Files.createTempDirectory("vet");
    FileSystem fileSystem = new DefaultFileSystem(tempDir);
    tested = new GerritConfigurationRepository(fileSystem);
  }

  @Test
  public void GIVEN_no_file_WHEN_read_THEN_it_should_return_empty_conf() {
    GerritConfiguration gerritConfiguration = tested.read();
    assertThat(gerritConfiguration).isNotNull();
    assertThat(gerritConfiguration.getSites()).isEmpty();
  }

  @Test
  public void
      GIVEN_file_containing_one_site_conf_WHEN_read_THEN_it_should_return_a_conf_with_one_site_conf() {
    GerritConfiguration config = new GerritConfiguration();
    GerritSiteConfiguration siteConfig = new GerritSiteConfiguration();
    siteConfig.setHttpUrl(URL);
    siteConfig.setHttpLogin(LOGIN);
    siteConfig.setHttpPassword(PASSWORD);
    config.setSites(Collections.singletonMap(URL, siteConfig));
    tested.write(config);

    GerritConfiguration gerritConfiguration = tested.read();
    assertThat(gerritConfiguration).isNotNull();
    assertThat(gerritConfiguration.getSites()).hasSize(1);

    GerritSiteConfiguration readSite = gerritConfiguration.getSites().get(URL);
    assertThat(readSite.getHttpUrl()).isEqualTo(URL);
    assertThat(readSite.getHttpLogin()).isEqualTo(LOGIN);
    assertThat(readSite.getHttpPassword()).isEqualTo(PASSWORD);
  }
}
