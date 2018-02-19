package com.cosium.vet.gerrit;

import java.util.Collections;
import java.util.Map;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class GerritConfiguration {

  /** Site by http url */
  private Map<String, GerritSiteConfiguration> sites;

  GerritConfiguration() {
    this.sites = Collections.emptyMap();
  }

  public Map<String, GerritSiteConfiguration> getSites() {
    return sites;
  }

  public void setSites(Map<String, GerritSiteConfiguration> sites) {
    if (sites == null) {
      this.sites = Collections.emptyMap();
    } else {
      this.sites = Collections.unmodifiableMap(sites);
    }
  }
}
