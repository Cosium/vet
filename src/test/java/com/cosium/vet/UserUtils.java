package com.cosium.vet;

import com.cosium.vet.runtime.CommandRunner;
import com.cosium.vet.runtime.Environments;

import java.nio.file.Paths;

import static java.util.Objects.requireNonNull;

/**
 * Created on 24/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class UserUtils {

  private final CommandRunner runner;

  UserUtils() {
    this(new TestCommandRunner());
  }

  UserUtils(CommandRunner runner) {
    requireNonNull(runner);
    this.runner = runner;
  }

  String getCurrentUserId() {
    String uid;
    try {
      uid =
          runner.run(
              Paths.get(System.getProperty("user.dir")),
              Environments.empty(),
              "id",
              "-u",
              System.getProperty("user.name"));
    } catch (Throwable t) {
      uid = null;
    }
    return uid;
  }
}
