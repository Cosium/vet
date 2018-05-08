package com.cosium.vet.gerrit;

import com.cosium.vet.git.CommitMessage;

import java.util.Optional;

/**
 * Created on 27/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GerritPatchSetRepository {

  /**
   * @param pushUrl The Gerrit push url
   * @param changeNumericId The change numeric id
   * @return The latest patch set commit message of the provided change numeric id.
   */
  Optional<CommitMessage> getLastestPatchSetCommitMessage(
      GerritPushUrl pushUrl, ChangeNumericId changeNumericId);
}
