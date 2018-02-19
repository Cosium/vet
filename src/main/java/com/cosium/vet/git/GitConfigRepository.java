package com.cosium.vet.git;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GitConfigRepository {

  String getValue(String key);

  void setValue(String key, String value);
}
