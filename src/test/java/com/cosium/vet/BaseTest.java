package com.cosium.vet;

import com.cosium.vet.git.DefaultGitClientFactory;
import org.junit.BeforeClass;

/**
 * Created on 17/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public abstract class BaseTest {

  @BeforeClass
  public static void beforeClass() {
    System.setProperty(DefaultGitClientFactory.USE_DOCKER_GIT, String.valueOf(true));
  }
}
