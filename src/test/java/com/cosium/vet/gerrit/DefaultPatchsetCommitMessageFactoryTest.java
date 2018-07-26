package com.cosium.vet.gerrit;

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
public class DefaultPatchsetCommitMessageFactoryTest {

  private static final String HELLO_WORLD = "Hello World";

  private static final String HELLO_WORLD_MULTI_LINES = "Hello\n\nWorld\n!";

  private GitClient git;

  private DefaultPatchsetCommitMessageFactory tested;

  @Before
  public void before() {
    git = mock(GitClient.class);
    tested = new DefaultPatchsetCommitMessageFactory(git);
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
  public void GIVEN_existing_change_WHEN_existing_message_contains_blank_lines_THEN_the_new_message_should_preserve_them() {
    when(git.getLastCommitMessage()).thenReturn(CommitMessage.of(HELLO_WORLD_MULTI_LINES));

    CommitMessage commitMessage = tested.build(null);
    assertThat(commitMessage.toString()).contains(HELLO_WORLD_MULTI_LINES);
  }

  @Test
  public void WHEN_changeid_I1234_THEN_commit_message_should_end_with_I1234() {
    Patchset patch = mock(Patchset.class);
    when(patch.getCommitMessage()).thenReturn(CommitMessage.of(HELLO_WORLD + "\nChange-Id: I1234"));

    CommitMessage commitMessage = tested.build(patch);
    assertThat(commitMessage.toString()).endsWith("\nChange-Id: I1234");
  }

}
