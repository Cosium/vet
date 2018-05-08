package com.cosium.vet.gerrit;

import com.cosium.vet.VetVersion;
import com.cosium.vet.git.CommitMessage;
import com.cosium.vet.git.GitClient;

import static java.util.Objects.requireNonNull;

/**
 * Created on 08/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
class DefaultPatchSetCommitMessageFactory implements PatchSetCommitMessageFactory {

  private static final String COMMIT_MESSAGE_VET_VERSION_PREFIX = "Vet-Version: ";
  private static final String COMMIT_MESSAGE_CHANGE_ID_PREFIX = "Change-Id: ";

  private final GitClient git;
  private final GerritPatchSetRepository patchSetRepository;

  DefaultPatchSetCommitMessageFactory(GitClient git, GerritPatchSetRepository patchSetRepository) {
    this.git = requireNonNull(git);
    this.patchSetRepository = requireNonNull(patchSetRepository);
  }

  @Override
  public CommitMessage build(GerritPushUrl pushUrl, ChangeNumericId numericId) {
    CommitMessage commitMessage =
        patchSetRepository
            .getLastestPatchSetCommitMessage(pushUrl, numericId)
            .orElseGet(git::getLastCommitMessage);

    String body =
        commitMessage.removeLinesStartingWith(
            COMMIT_MESSAGE_VET_VERSION_PREFIX, COMMIT_MESSAGE_CHANGE_ID_PREFIX);

    String footer =
        String.join(
            "\n",
            COMMIT_MESSAGE_VET_VERSION_PREFIX + VetVersion.VALUE,
            COMMIT_MESSAGE_CHANGE_ID_PREFIX + parseOrBuildChangeChangeId(commitMessage));

    return CommitMessage.of(body + "\n\n" + footer);
  }

  private String parseOrBuildChangeChangeId(CommitMessage commitMessage) {
    // TODO
    return null;
  }
}
