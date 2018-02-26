package com.cosium.vet;

import com.cosium.vet.log.LoggerFactory;

public class App {

  public static void main(String[] args) {
    LoggerFactory.setPrintStackTrace(false);
    LoggerFactory.setPrintContext(false);
    new Vet(true).run(args);
  }
}
