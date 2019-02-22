package com.cosium.vet.git;

import com.cosium.vet.runtime.CommandRunner;
import com.cosium.vet.utils.OperatingSystem;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created on 30/06/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class GitProviderTest {

  private Path repositoryDirectory;
  private CommandRunner commandRunner;
  private OperatingSystem operatingSystem;
  private GitProvider tested;

  @Before
  public void before() {
    repositoryDirectory = mock(Path.class);
    commandRunner = mock(CommandRunner.class);
    operatingSystem = mock(OperatingSystem.class);
    tested = new GitProvider(operatingSystem, repositoryDirectory, commandRunner, false);
  }

  @Test
  public void
      GIVEN_non_windows_os_WHEN_build_THEN_it_should_return_an_instance_of_basic_git_client() {
    when(operatingSystem.isWindows()).thenReturn(false);
    GitClient gitClient = tested.build();
    assertThat(gitClient).isNotInstanceOf(WindowsGitClient.class);
  }

  @Test
  public void
      GIVEN_windows_os_WHEN_build_THEN_it_should_return_an_instance_of_windows_git_client() {
    when(operatingSystem.isWindows()).thenReturn(true);
    GitClient gitClient = tested.build();
    assertThat(gitClient).isInstanceOf(WindowsGitClient.class);
  }
}
