package com.cosium.vet.git;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GitClientFactory {
  /** @return A new Git client */
  GitClient buildClient();
}
