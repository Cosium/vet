package com.cosium.vet.gerrit;

import com.google.gerrit.extensions.api.GerritApi;

import static java.util.Objects.requireNonNull;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class DefaultGerritClient implements GerritClient {

  private final GerritApi gerritApi;

  DefaultGerritClient(GerritApi gerritApi) {
    requireNonNull(gerritApi);
    this.gerritApi = gerritApi;
  }


}
