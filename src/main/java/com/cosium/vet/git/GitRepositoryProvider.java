package com.cosium.vet.git;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GitRepositoryProvider {
  /** @return The current git repository */
  GitRepository getRepository();
}
