package com.cosium.vet.gerrit;

import com.google.gerrit.extensions.api.GerritApi;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GerritClientProvider {

  /** @return A ready to use gerrit api client */
  GerritApi getApi();
}
