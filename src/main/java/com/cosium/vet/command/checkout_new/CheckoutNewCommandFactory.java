package com.cosium.vet.command.checkout_new;

import com.cosium.vet.gerrit.ChangeCheckoutBranchName;

/**
 * Created on 09/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface CheckoutNewCommandFactory {

  /**
   * @param force True to force the command execution without prompt
   * @param checkoutBranch The branch to checkout that will track the change
   * @return A new track command
   */
  CheckoutNewCommand build(Boolean force, ChangeCheckoutBranchName checkoutBranch);
}
