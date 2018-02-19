package com.cosium.vet.gerrit.config;

import java.util.Optional;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GerritConfiguration {

  /** @return The Gerrit issue id */
  Optional<String> getCurrentIssueId();

  /** Sets the Gerrit issue id */
  void setCurrentIssueId(String issueId);

  /** @return The selected site configuration if any selected */
  Optional<GerritSiteConfiguration> getSelectedSite();

  void selectSite(String httpUrl);

  void addSite(String httpUrl, String httpLogin, String httpPassword);
}
