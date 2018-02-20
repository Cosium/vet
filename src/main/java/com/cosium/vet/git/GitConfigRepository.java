package com.cosium.vet.git;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GitConfigRepository {

  /**
   * @param key The key to look for
   * @return The value associated to the provided key
   */
  String getCurrentBranchValue(String key);

  /**
   * @param key The key to set
   * @param value The value to set
   */
  void setCurrentBranchValue(String key, String value);
}
