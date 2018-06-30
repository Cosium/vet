package com.cosium.vet.git;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Created on 29/06/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class WindowsGitClientTest {

  private static final String TREE = "tree-test";
  private static final String PARENT = "parent-test";

  private GitClient delegate;
  private WindowsGitClient tested;

  @Before
  public void before() {
    delegate = mock(GitClient.class);
    tested = new WindowsGitClient(delegate);
  }

  @Test
  public void testCommitTree() {
    CommitMessage commitMessage = mock(CommitMessage.class);
    CommitMessage escapedCommitMessage = mock(CommitMessage.class);
    when(commitMessage.escapeQuotes()).thenReturn(escapedCommitMessage);
    tested.commitTree(TREE, PARENT, commitMessage);
    verify(delegate).commitTree(TREE, PARENT, escapedCommitMessage);
  }
}
