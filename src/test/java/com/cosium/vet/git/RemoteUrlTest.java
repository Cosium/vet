package com.cosium.vet.git;

import org.junit.Test;

/** @author Réda Housni Alaoui */
public class RemoteUrlTest {

  @Test
  public void createHttpsUrl() {
    RemoteUrl.of("https://foo/bar");
  }

  @Test
  public void createSshUrl() {
    RemoteUrl.of("ssh://foo:29418/bar");
  }
}
