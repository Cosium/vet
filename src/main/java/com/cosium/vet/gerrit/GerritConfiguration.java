package com.cosium.vet.gerrit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class GerritConfiguration {

  private final List<GerritSiteConfiguration> sites = new ArrayList<>();

  public List<GerritSiteConfiguration> getSites() {
    return sites;
  }
}
