package com.cosium.vet.gerrit.config;

import java.util.Optional;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GerritConfiguration {

  /** @return The Gerrit change number */
  Optional<String> getCurrentChangeId();

  /** Sets the Gerrit change number */
  void setCurrentChangeId(String changeId);

  /** @return The selected site configuration if any selected */
  Optional<GerritSiteConfiguration> getSelectedSite();

  void selectSite(String httpUrl);

  void addSite(String httpUrl, String httpLogin, String httpPassword);
}
