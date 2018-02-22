package com.cosium.vet.gerrit;

/**
 * Created on 22/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
interface GerritCredentials {

  /** Mark the credentials as invalid */
  void invalidate();

  /** @return The gerrit http root url */
  GerritHttpRootUrl getHttpRootUrl();

  /** @return The gerrit user */
  GerritUser getUser();

  /** @return The gerrit password */
  GerritPassword getPassword();
}
