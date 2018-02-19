package com.cosium.vet.gerrit;

import java.util.Collections;
import java.util.List;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class GerritConfiguration {

  private List<GerritSiteConfiguration> sites;

  GerritConfiguration() {
    this.sites = Collections.emptyList();
  }

  public List<GerritSiteConfiguration> getSites() {
    return sites;
  }

  public void setSites(List<GerritSiteConfiguration> sites) {
    if (sites == null) {
      this.sites = Collections.emptyList();
    } else {
      this.sites = Collections.unmodifiableList(sites);
    }
  }
}
