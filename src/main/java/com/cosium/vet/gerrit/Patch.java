package com.cosium.vet.gerrit;

import com.cosium.vet.git.CommitMessage;
import com.cosium.vet.git.RevisionId;

import java.util.Optional;

/**
 * Created on 09/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface Patch {

  /** @return The patch number in the change */
  int getNumber();

  /** @return The change numeric id */
  ChangeNumericId getChangeNumericId();

  /** @return The commit message */
  CommitMessage getCommitMessage();

  /** @return The parent revision id */
  RevisionId getParent();

  /** @return The log emitted during creation */
  Optional<String> getCreationLog();
}
