package com.cosium.vet.gerrit.config;

import com.cosium.vet.gerrit.ChangeId;
import com.cosium.vet.gerrit.GerritHttpRootUrl;

import java.util.Optional;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GerritConfiguration {

  /** @return The Gerrit change id */
  Optional<ChangeId> getChangeId();

  /** Sets the Gerrit change id */
  void setChangeId(ChangeId changeId);

  /**
   * Create or update a site auth configuration
   *
   * @param httpUrl The site url
   * @param httpLogin The site login
   * @param httpPassword The site password
   */
  GerritSiteAuthConfiguration setAndGetSiteAuth(
      GerritHttpRootUrl httpUrl, String httpLogin, String httpPassword);

  /**
   * @param httpUrl The http url of the site
   * @return The site auth configuration if it exists
   */
  Optional<GerritSiteAuthConfiguration> getSiteAuth(GerritHttpRootUrl httpUrl);

  /** @param httpUrl The http url of the site auth to drop */
  void dropSiteAuth(GerritHttpRootUrl httpUrl);
}
