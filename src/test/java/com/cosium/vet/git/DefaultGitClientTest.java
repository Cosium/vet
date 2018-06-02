package com.cosium.vet.git;

import com.cosium.vet.runtime.CommandRunner;
import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created on 02/06/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultGitClientTest {

  private static final RemoteUrl REMOTE_URL = RemoteUrl.of("https://foo.com/test-project");
  private static final RemoteUrl REMOTE_PUSH_URL =
      RemoteUrl.of("https://foo-review.com/test-project");

  private Path repositoryDirectory;
  private CommandRunner commandRunner;
  private GitConfigRepository gitConfigRepository;

  private DefaultGitClient tested;

  @Before
  public void before() {
    repositoryDirectory = mock(Path.class);
    commandRunner = mock(CommandRunner.class);
    gitConfigRepository = mock(GitConfigRepository.class);

    tested = new DefaultGitClient(repositoryDirectory, commandRunner, gitConfigRepository);
  }

  @Test
  public void WHEN_pushurl_is_set_THEN_remoteurl_is_remotepushurl() {
    when(gitConfigRepository.getValue("remote.origin.pushurl"))
        .thenReturn(REMOTE_PUSH_URL.toString());
    when(gitConfigRepository.getValue("remote.origin.url")).thenReturn(null);
    assertThat(tested.getRemotePushUrl(RemoteName.ORIGIN)).contains(REMOTE_PUSH_URL);
  }

  @Test
  public void WHEN_pushurl_is_not_set_THEN_remoteurl_is_remoteurl() {
    when(gitConfigRepository.getValue("remote.origin.pushurl")).thenReturn(StringUtils.EMPTY);
    when(gitConfigRepository.getValue("remote.origin.url")).thenReturn(REMOTE_URL.toString());
    assertThat(tested.getRemotePushUrl(RemoteName.ORIGIN)).contains(REMOTE_URL);
  }

  @Test
  public void WHEN_no_remote_set_THEN_remoteurl_is_empty() {
    assertThat(tested.getRemotePushUrl(RemoteName.ORIGIN)).isEmpty();
  }
}
