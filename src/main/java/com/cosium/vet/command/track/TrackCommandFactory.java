package com.cosium.vet.command.track;

import com.cosium.vet.gerrit.ChangeNumericId;
import com.cosium.vet.git.BranchShortName;

/**
 * Created on 09/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface TrackCommandFactory {

  /**
   * @param force True to force the command execution without prompt
   * @param numericId The numeric id of the change to track
   * @param branchShortName The target branch of the change to track
   * @return A new track command
   */
  TrackCommand build(Boolean force, ChangeNumericId numericId, BranchShortName branchShortName);
}
