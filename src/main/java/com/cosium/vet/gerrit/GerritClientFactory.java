package com.cosium.vet.gerrit;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GerritClientFactory {

  /**
   * @param user The optional gerrit user
   * @param password The optional gerrit password
   * @return A new ready to use gerrit client
   */
  GerritClient build(GerritUser user, GerritPassword password);
}
