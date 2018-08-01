package com.cosium.vet.gerrit;

import com.cosium.vet.git.RevisionId;

/**
 * Created on 23/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface Change {

  /** @return The numeric id of this change */
  ChangeNumericId getNumericId();

  /** @return The current revision of this change */
  RevisionId fetchRevision();

  /** @return The parent revision of this change */
  RevisionId fetchParent();

  /** @return The change web url */
  String getWebUrl();
}
