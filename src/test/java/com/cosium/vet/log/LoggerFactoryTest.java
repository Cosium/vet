package com.cosium.vet.log;

import org.junit.Test;

/**
 * Created on 25/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class LoggerFactoryTest {

  @Test
  public void testLogInfo() {
    LoggerFactory.getLogger(LoggerFactoryTest.class)
        .info("Hello {} {}, how are you?", "John", "Doe");
  }

  @Test
  public void testLogError() {
    LoggerFactory.setPrintContext(false);
    LoggerFactory.setPrintStackTrace(false);
    LoggerFactory.getLogger(LoggerFactoryTest.class)
        .error("Hello {} {}, how are you?", "John", "Doe", new RuntimeException("Not good"));
  }
}
