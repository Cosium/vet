package com.cosium.vet.command.checkout;

import com.cosium.vet.command.VetCommand;
import com.cosium.vet.gerrit.Change;
import com.cosium.vet.gerrit.ChangeNumericId;
import com.cosium.vet.gerrit.ChangeRepository;
import com.cosium.vet.gerrit.ChangeRepositoryFactory;
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
public class CheckoutCommand implements VetCommand {

  private final GitClient git;
  private final ChangeRepository changeRepository;
  private final UserInput userInput;
  private final UserOutput userOutput;

  private final boolean force;
  private final ChangeNumericId numericId;
  private final BranchShortName targetBranch;
  private final BranchShortName checkoutBranch;

  private CheckoutCommand(
      GitClient git,
      ChangeRepository changeRepository,
      UserInput userInput,
      UserOutput userOutput,
      // Optionals
      Boolean force,
      ChangeNumericId numericId,
      BranchShortName targetBranch,
      BranchShortName checkoutBranch) {
    this.git = requireNonNull(git);
    this.changeRepository = requireNonNull(changeRepository);
    this.userInput = requireNonNull(userInput);
    this.userOutput = requireNonNull(userOutput);

    this.force = BooleanUtils.toBoolean(force);
    this.numericId = numericId;
    this.targetBranch = targetBranch;
    this.checkoutBranch = checkoutBranch;
  }

  @Override
  public void execute() {
    ChangeNumericId numericId = getNumericId();
    if (!changeRepository.exists(numericId)) {
      userOutput.display(
          "Could not find any change identified by " + numericId + " on Gerrit. Aborting.");
      return;
    }
    BranchShortName targetBranch = getTargetBranch();
    BranchShortName checkoutBranch = getCheckoutBranch(numericId);
    if (!confirm(numericId, targetBranch, checkoutBranch)) {
      return;
    }

    Change change =
        changeRepository.checkoutAndTrackChange(checkoutBranch, numericId, targetBranch);

    userOutput.display(git.status());
    userOutput.display("Now tracking change " + change);
  }

  private boolean confirm(
      ChangeNumericId numericId, BranchShortName targetBranch, BranchShortName checkoutBranch) {
    if (force) {
      return true;
    }
    return userInput.askYesNo(
        "Branch '"
            + checkoutBranch
            + "' will be checkout from change id "
            + numericId
            + " to target branch '"
            + targetBranch
            + "'.\nDo you want to continue?",
        false);
  }

  private ChangeNumericId getNumericId() {
    return ofNullable(numericId)
        .orElseGet(() -> ChangeNumericId.of(userInput.askLong("Change numeric ID")));
  }

  private BranchShortName getTargetBranch() {
    return ofNullable(targetBranch)
        .orElseGet(
            () ->
                BranchShortName.of(
                    userInput.askNonBlank("Target branch", BranchShortName.MASTER.toString())));
  }

  private BranchShortName getCheckoutBranch(ChangeNumericId numericId) {
    return ofNullable(checkoutBranch)
        .orElseGet(
            () ->
                BranchShortName.of(
                    userInput.askNonBlank("Checkout branch", "change/" + numericId)));
  }

  public static class Factory implements CheckoutCommandFactory {

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
    public CheckoutCommand build(
        Boolean force,
        BranchShortName checkoutBranch,
        ChangeNumericId numericId,
        BranchShortName targetBranch) {
      return new CheckoutCommand(
          gitProvider.build(),
          changeRepositoryFactory.build(),
          userInput,
          userOutput,
          force,
          numericId,
          targetBranch,
          checkoutBranch);
    }
  }
}
