package com.cosium.vet.gerrit;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GerritChangeRepositoryFactory {

  /** @return A new ready to use gerrit client */
  GerritChangeRepository build();
}