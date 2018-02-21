package com.cosium.vet.gerrit.config;

import java.util.function.Function;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GerritConfigurationRepository {

  GerritConfiguration read();

  <T> T readAndWrite(Function<GerritConfiguration, T> functor);
}
