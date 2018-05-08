package com.cosium.vet.gerrit;

import com.cosium.vet.VetVersion;
import com.cosium.vet.git.CommitMessage;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.thirdparty.apache_commons_codec.DigestUtils;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
  public CommitMessage build(ChangeNumericId numericId) {
    CommitMessage commitMessage =
        patchSetRepository
            .getLastestPatchSetCommitMessage(numericId)
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
    Pattern pattern = Pattern.compile(Pattern.quote(COMMIT_MESSAGE_CHANGE_ID_PREFIX) + "(.*)");
    Matcher matcher = pattern.matcher(commitMessage.toString());
    if (matcher.find()) {
      return matcher.group(1);
    }

    return "I"
        + DigestUtils.shaHex(String.format("%s|%s", UUID.randomUUID(), commitMessage.toString()));
  }
}
