package com.cosium.vet.command.create;

import com.cosium.vet.git.BranchShortName;

/**
 * Created on 09/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface CreateCommandFactory {

  /**
   * @param force True to force the command execution without prompt
   * @param targetBranch The target branch of the change
   * @return A new command
   */
  CreateCommand build(Boolean force, BranchShortName targetBranch);
}
