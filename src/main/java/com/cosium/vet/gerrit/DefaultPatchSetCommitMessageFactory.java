package com.cosium.vet.gerrit;

import com.cosium.vet.VetVersion;
import com.cosium.vet.git.CommitMessage;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;
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

  private static final Logger LOG =
      LoggerFactory.getLogger(DefaultPatchSetCommitMessageFactory.class);

  private static final String COMMIT_MESSAGE_VET_VERSION_PREFIX = "Vet-Version: ";
  private static final String COMMIT_MESSAGE_CHANGE_ID_PREFIX = "Change-Id: ";

  private final GitClient git;

  DefaultPatchSetCommitMessageFactory(GitClient git) {
    this.git = requireNonNull(git);
  }

  @Override
  public CommitMessage build(Patch latestPatch) {
    String changeChangeId;
    CommitMessage commitMessage;
    if (latestPatch == null) {
      commitMessage = git.getLastCommitMessage();
      changeChangeId = generateChangeChangeId(commitMessage);
    } else {
      commitMessage = latestPatch.getCommitMessage();
      changeChangeId = parseChangeChangeId(commitMessage);
    }

    String body =
        commitMessage.removeLinesStartingWith(
            COMMIT_MESSAGE_VET_VERSION_PREFIX, COMMIT_MESSAGE_CHANGE_ID_PREFIX);

    String footer =
        String.join(
            "\n",
            COMMIT_MESSAGE_VET_VERSION_PREFIX + VetVersion.VALUE,
            COMMIT_MESSAGE_CHANGE_ID_PREFIX + changeChangeId);

    return CommitMessage.of(body + "\n\n" + footer);
  }

  private String generateChangeChangeId(CommitMessage commitMessage) {
    String changeId =
        "I"
            + DigestUtils.shaHex(
                String.format("%s|%s", UUID.randomUUID(), commitMessage.toString()));
    LOG.debug("Generated change change id '{}'", changeId);
    return changeId;
  }

  private String parseChangeChangeId(CommitMessage commitMessage) {
    Pattern pattern = Pattern.compile(Pattern.quote(COMMIT_MESSAGE_CHANGE_ID_PREFIX) + "(.*)");
    Matcher matcher = pattern.matcher(commitMessage.toString());
    if (!matcher.find()) {
      throw new RuntimeException(
          "Could not parse any change id from commit message '" + commitMessage + "'");
    }
    String changeId = matcher.group(1);
    LOG.debug("Found change change id '{}'", changeId);
    return changeId;
  }
}
