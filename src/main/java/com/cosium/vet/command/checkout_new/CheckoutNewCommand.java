package com.cosium.vet.command.checkout_new;

import com.cosium.vet.command.VetCommand;
import com.cosium.vet.gerrit.*;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.GitProvider;
import com.cosium.vet.runtime.UserInput;
import com.cosium.vet.runtime.UserOutput;
import com.cosium.vet.thirdparty.apache_commons_lang3.BooleanUtils;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * Created on 09/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class CheckoutNewCommand implements VetCommand {

  private final GitClient git;
  private final ChangeRepository changeRepository;
  private final UserInput userInput;
  private final UserOutput userOutput;

  private final boolean force;
  private final BranchShortName targetBranch;
  private final ChangeCheckoutBranchName checkoutBranch;

  private CheckoutNewCommand(
      GitClient git,
      ChangeRepository changeRepository,
      UserInput userInput,
      UserOutput userOutput,
      // Optionals
      Boolean force,
      BranchShortName targetBranch,
      ChangeCheckoutBranchName checkoutBranch) {
    this.git = requireNonNull(git);
    this.changeRepository = requireNonNull(changeRepository);
    this.userInput = requireNonNull(userInput);
    this.userOutput = requireNonNull(userOutput);

    this.force = BooleanUtils.toBoolean(force);
    this.targetBranch = targetBranch;
    this.checkoutBranch = checkoutBranch;
  }

  @Override
  public void execute() {
    BranchShortName targetBranch = getTargetBranch();
    Change change = changeRepository.createChange(targetBranch);
    ChangeNumericId numericId = change.getNumericId();

    userOutput.display("Change " + change + " created");

    ChangeCheckoutBranchName checkoutBranch = getCheckoutBranch(numericId);
    if (!confirm(change, checkoutBranch)) {
      return;
    }
    changeRepository.checkoutAndTrackChange(checkoutBranch, numericId, targetBranch);
    userOutput.display(git.status());
    userOutput.display("Now tracking new change " + change);
  }

  private boolean confirm(Change change, ChangeCheckoutBranchName checkoutBranch) {
    if (force) {
      return true;
    }
    return userInput.askYesNo(
        "Branch '"
            + checkoutBranch
            + "' will be checkout from change "
            + change
            + ".\nDo you want to continue?",
        true);
  }

  private BranchShortName getTargetBranch() {
    return ofNullable(targetBranch)
        .orElseGet(
            () ->
                BranchShortName.of(
                    userInput.askNonBlank("Target branch", BranchShortName.MASTER.toString())));
  }

  private ChangeCheckoutBranchName getCheckoutBranch(ChangeNumericId numericId) {
    return ofNullable(checkoutBranch)
        .orElseGet(
            () ->
                ChangeCheckoutBranchName.of(
                    userInput.askNonBlank(
                        "Checkout branch",
                        ChangeCheckoutBranchName.defaults(numericId).toString())));
  }

  public static class Factory implements CheckoutNewCommandFactory {

    private final GitProvider gitProvider;
    private final ChangeRepositoryFactory changeRepositoryFactory;
    private final UserInput userInput;
    private final UserOutput userOutput;

    public Factory(
        GitProvider gitProvider,
        ChangeRepositoryFactory changeRepositoryFactory,
        UserInput userInput,
        UserOutput userOutput) {
      this.gitProvider = requireNonNull(gitProvider);
      this.changeRepositoryFactory = requireNonNull(changeRepositoryFactory);
      this.userInput = requireNonNull(userInput);
      this.userOutput = requireNonNull(userOutput);
    }

    @Override
    public CheckoutNewCommand build(
        Boolean force, ChangeCheckoutBranchName checkoutBranch, BranchShortName targetBranch) {
      return new CheckoutNewCommand(
          gitProvider.build(),
          changeRepositoryFactory.build(),
          userInput,
          userOutput,
          force,
          targetBranch,
          checkoutBranch);
    }
  }
}