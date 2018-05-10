package com.cosium.vet.command.fire_and_forget;

import com.cosium.vet.git.BranchShortName;

/**
 * Created on 09/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface FireAndForgetCommandFactory {

  /**
   * @param force True to force the command execution without prompt
   * @param targetBranch The target branch of the change
   * @return A new command
   */
  FireAndForgetCommand build(Boolean force, BranchShortName targetBranch);
}
