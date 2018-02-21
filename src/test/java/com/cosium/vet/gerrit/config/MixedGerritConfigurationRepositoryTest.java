package com.cosium.vet.gerrit.config;

import com.cosium.vet.file.FileSystem;
import com.cosium.vet.gerrit.ChangeId;
import com.cosium.vet.gerrit.GerritHttpRootUrl;
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
  private static final ChangeId CHANGE_ID = ChangeId.of("baz~1234");

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
  public void GIVEN_conf_containing_one_site_and_change_conf_WHEN_read_THEN_it_should_match() {
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
    when(gitConfigProvider.getCurrentBranchValue("vet-change-id"))
        .thenReturn(CHANGE_ID.value());

    GerritConfiguration gerritConfiguration = tested.read();
    assertThat(gerritConfiguration).isNotNull();
    assertThat(gerritConfiguration.getChangeId()).contains(CHANGE_ID);

    Optional<GerritSiteAuthConfiguration> siteConf =
        gerritConfiguration.getSiteAuth(GerritHttpRootUrl.of(URL));
    if (!siteConf.isPresent()) {
      fail("Expected non null site conf");
    }

    GerritSiteAuthConfiguration finalSiteConf = siteConf.get();
    assertThat(finalSiteConf.getHttpUrl()).isEqualTo(URL);
    assertThat(finalSiteConf.getHttpLogin()).isEqualTo(LOGIN);
    assertThat(finalSiteConf.getHttpPassword()).isEqualTo(PASSWORD);
  }
}
