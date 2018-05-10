package com.cosium.vet.gerrit;

import com.cosium.vet.git.CommitMessage;

import java.util.Optional;

/**
 * Created on 09/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface Patch {

  /** @return The patch id in the change */
  int getId();

  /** @return The change numeric id */
  ChangeNumericId getChangeNumericId();

  /** @return The commit message */
  CommitMessage getCommitMessage();

  /** @return The log emitted during creation */
  Optional<String> getCreationLog();
}
