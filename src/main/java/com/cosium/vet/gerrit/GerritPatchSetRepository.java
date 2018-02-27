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
   * @param changeChangeId The change change id
   * @return The latest patch set commit message of the provided change change id.
   */
  Optional<CommitMessage> getLastestPatchSetCommitMessage(GerritPushUrl pushUrl, ChangeChangeId changeChangeId);

}
