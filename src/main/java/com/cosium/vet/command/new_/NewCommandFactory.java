package com.cosium.vet.command.new_;

import com.cosium.vet.git.BranchShortName;

/**
 * Created on 09/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface NewCommandFactory {

  /**
   * @param force True to force the command execution without prompt
   * @param targetBranch The target branch of the change
   * @return A new command
   */
  NewCommand build(Boolean force, BranchShortName targetBranch);
}
