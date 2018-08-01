package com.cosium.vet.gerrit;

import com.cosium.vet.git.CommitMessage;
import com.cosium.vet.git.RevisionId;

/**
 * Created on 09/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface Patchset {

  /** @return The patchset number in the change */
  int getNumber();

  /** @return The change numeric id */
  ChangeNumericId getChangeNumericId();

  /** @return The commit message */
  CommitMessage getCommitMessage();

  /** @return The patchset revision id */
  RevisionId getRevision();

  /** @return The parent revision id */
  RevisionId getParent();
}
