package com.cosium.vet.gerrit;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class GerritSiteConfiguration {

  private String httpUrl;
  private String httpLogin;
  private String httpPassword;

  public String getHttpUrl() {
    return httpUrl;
  }

  public void setHttpUrl(String httpUrl) {
    this.httpUrl = httpUrl;
  }

  public String getHttpLogin() {
    return httpLogin;
  }

  public void setHttpLogin(String httpLogin) {
    this.httpLogin = httpLogin;
  }

  public String getHttpPassword() {
    return httpPassword;
  }

  public void setHttpPassword(String httpPassword) {
    this.httpPassword = httpPassword;
  }
}
