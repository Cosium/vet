package com.cosium.gerrit.vet.push;

import com.cosium.gerrit.vet.VetCommand;

/**
 * Created on 14/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class PushCommand implements VetCommand {

  private final String branchName;

  PushCommand(String branchName) {
    this.branchName = branchName;
  }

  @Override
  public void execute() {



  }
}
