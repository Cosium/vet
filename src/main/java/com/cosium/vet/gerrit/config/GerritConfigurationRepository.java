package com.cosium.vet.gerrit.config;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GerritConfigurationRepository {

  GerritConfiguration read();

  void write(GerritConfiguration gerritConfiguration);
}
