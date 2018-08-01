package com.cosium.vet.gerrit;

import com.cosium.vet.git.RevisionId;

/**
 * Created on 01/08/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface ChangeParent {

  /** @return The revision of the parent */
  RevisionId getRevision();
}
