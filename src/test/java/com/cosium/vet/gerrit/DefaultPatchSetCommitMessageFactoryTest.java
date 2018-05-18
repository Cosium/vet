package com.cosium.vet.gerrit;

import com.cosium.vet.VetVersion;
import com.cosium.vet.git.CommitMessage;
import com.cosium.vet.git.GitClient;
import org.junit.Before;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created on 08/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultPatchSetCommitMessageFactoryTest {

  private static final String HELLO_WORLD = "Hello World";

  private GitClient git;

  private DefaultPatchSetCommitMessageFactory tested;

  @Before
  public void before() {
    git = mock(GitClient.class);
    tested = new DefaultPatchSetCommitMessageFactory(git);
  }

  @Test
  public void
      GIVEN_no_existing_changeid_WHEN_build_commitmessage_THEN_the_change_id_is_part_of_the_message() {
    when(git.getLastCommitMessage()).thenReturn(CommitMessage.of(HELLO_WORLD));

    CommitMessage commitMessage = tested.build(null);
    String rawMessage = commitMessage.toString();

    Pattern pattern = Pattern.compile(Pattern.compile("Change-Id: ") + "I(.*)");
    Matcher matcher = pattern.matcher(rawMessage);
    assertThat(matcher.find()).isTrue();
    assertThat(matcher.group(1)).isNotBlank();
  }

  @Test
  public void
      WHEN_last_commit_message_hello_world_THEN_commit_message_should_contain_hello_world() {
    when(git.getLastCommitMessage()).thenReturn(CommitMessage.of(HELLO_WORLD));

    CommitMessage commitMessage = tested.build(null);
    assertThat(commitMessage.toString()).contains(HELLO_WORLD);
  }

  @Test
  public void WHEN_changeid_I1234_THEN_commit_message_should_end_with_I1234() {
    Patch patch = mock(Patch.class);
    when(patch.getCommitMessage()).thenReturn(CommitMessage.of(HELLO_WORLD + "\nChange-Id: I1234"));

    CommitMessage commitMessage = tested.build(patch);
    assertThat(commitMessage.toString()).endsWith("\nChange-Id: I1234");
  }

  @Test
  public void WHEN_existing_change_THEN_commit_message_contains_VET_VERSION() {
    when(git.getLastCommitMessage()).thenReturn(CommitMessage.of(HELLO_WORLD));

    CommitMessage commitMessage = tested.build(null);
    assertThat(commitMessage.toString()).contains("\nVet-Version: " + VetVersion.getValue());
  }
}
