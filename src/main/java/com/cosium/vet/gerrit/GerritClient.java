package com.cosium.vet.gerrit;

import java.util.Optional;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GerritClient {

  /** @return The current change id */
  Optional<ChangeId> getChangeId();

  /**
   * @param changeDescription The change description
   * @return The created and set change id
   */
  ChangeId createAndSetChangeId(String changeDescription);
}
