package com.cosium.vet.gerrit.config;

import com.cosium.vet.file.FileSystem;
import com.cosium.vet.git.GitConfigRepository;
import com.fasterxml.jackson.jr.ob.JSON;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class MixedGerritConfigurationRepositoryTest {

  private static final String URL = "https://foo.com";
  private static final String LOGIN = "foo";
  private static final String PASSWORD = "bar";
  private static final String ISSUE_ID = "1234";

  private FileSystem fileSystem;
  private GitConfigRepository gitConfigProvider;
  private MixedGerritConfigurationRepository tested;

  @Before
  public void before() {
    fileSystem = mock(FileSystem.class);
    when(fileSystem.newAppFileInputStream(any()))
        .then(invocation -> new ByteArrayInputStream("".getBytes()));

    gitConfigProvider = mock(GitConfigRepository.class);
    tested = new MixedGerritConfigurationRepository(fileSystem, gitConfigProvider);
  }

  @Test
  public void GIVEN_no_file_WHEN_read_THEN_it_should_return_empty_conf() {
    GerritConfiguration gerritConfiguration = tested.read();
    assertThat(gerritConfiguration).isNotNull();
  }

  @Test
  public void GIVEN_conf_containing_one_site_and_issue_conf_WHEN_read_THEN_it_should_match() {
    when(fileSystem.newAppFileInputStream(any()))
        .then(
            invocation ->
                new ByteArrayInputStream(
                    JSON.std
                        .with(JSON.Feature.PRETTY_PRINT_OUTPUT)
                        .composeBytes()
                        .startObject()
                        .startObjectField("sites")
                        .startObjectField(URL)
                        .put("httpUrl", URL)
                        .put("httpLogin", LOGIN)
                        .put("httpPassword", PASSWORD)
                        .end()
                        .end()
                        .end()
                        .finish()));
    when(gitConfigProvider.getValue("vet-current-issue-id")).thenReturn(ISSUE_ID);
    when(gitConfigProvider.getValue("vet-selected-site-http-url")).thenReturn(URL);

    GerritConfiguration gerritConfiguration = tested.read();
    assertThat(gerritConfiguration).isNotNull();
    assertThat(gerritConfiguration.getCurrentIssueId()).contains(ISSUE_ID);

    Optional<GerritSiteConfiguration> siteConf = gerritConfiguration.getSelectedSite();
    if (!siteConf.isPresent()) {
      fail("Expected non null site conf");
    }

    GerritSiteConfiguration finalSiteConf = siteConf.get();
    assertThat(finalSiteConf.getHttpUrl()).isEqualTo(URL);
    assertThat(finalSiteConf.getHttpLogin()).isEqualTo(LOGIN);
    assertThat(finalSiteConf.getHttpPassword()).isEqualTo(PASSWORD);
  }

}
