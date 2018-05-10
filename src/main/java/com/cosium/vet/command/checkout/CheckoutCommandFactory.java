package com.cosium.vet.command.checkout;

import com.cosium.vet.gerrit.ChangeNumericId;
import com.cosium.vet.git.BranchShortName;

/**
 * Created on 09/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface CheckoutCommandFactory {

  /**
   * @param force True to force the command execution without prompt
   * @param numericId The numeric id of the change to track
   * @param branchShortName The target branch of the change to track
   * @param checkoutBranch The branch to checkout that will track the change
   * @return A new track command
   */
  CheckoutCommand build(
      Boolean force,
      ChangeNumericId numericId,
      BranchShortName branchShortName,
      BranchShortName checkoutBranch);
}
