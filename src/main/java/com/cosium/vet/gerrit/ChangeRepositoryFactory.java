package com.cosium.vet.gerrit;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface ChangeRepositoryFactory {

  /** @return A new ready to use gerrit client */
  ChangeRepository build();
}
