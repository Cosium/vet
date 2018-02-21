package com.cosium.vet.gerrit.config;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GerritSiteAuthConfiguration {
  String getHttpUrl();

  void setHttpUrl(String httpUrl);

  String getHttpLogin();

  void setHttpLogin(String httpLogin);

  String getHttpPassword();

  void setHttpPassword(String httpPassword);
}
