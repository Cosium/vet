package com.cosium.vet.command.checkout_new;

import com.cosium.vet.command.VetCommand;
import com.cosium.vet.gerrit.*;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.GitProvider;
import com.cosium.vet.git.RevisionId;
import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;
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

  private static final Logger LOG = LoggerFactory.getLogger(CheckoutNewCommand.class);

  private final GitClient git;
  private final ChangeRepository changeRepository;
  private final UserInput userInput;
  private final UserOutput userOutput;

  private final boolean force;
  private final ChangeCheckoutBranchName checkoutBranch;

  private CheckoutNewCommand(
      GitClient git,
      ChangeRepository changeRepository,
      UserInput userInput,
      UserOutput userOutput,
      // Optionals
      Boolean force,
      ChangeCheckoutBranchName checkoutBranch) {
    this.git = requireNonNull(git);
    this.changeRepository = requireNonNull(changeRepository);
    this.userInput = requireNonNull(userInput);
    this.userOutput = requireNonNull(userOutput);

    this.force = BooleanUtils.toBoolean(force);
    this.checkoutBranch = checkoutBranch;
  }

  @Override
  public void execute() {
    BranchShortName targetBranch = git.getBranch();
    if (!confirm(targetBranch)) {
      LOG.debug("Confirmation not ok. Aborted.");
      return;
    }

    LOG.debug("Creating change with target branch '{}'", targetBranch);
    Change change = changeRepository.createChange(targetBranch, PatchOptions.DEFAULT);
    ChangeNumericId numericId = change.getNumericId();
    userOutput.display("Change " + change + " created");

    RevisionId parent = change.fetchParent();
    LOG.debug("Resetting to '{}'", parent);
    userOutput.display(git.resetKeep(parent));

    ChangeCheckoutBranchName checkoutBranch = getCheckoutBranch(numericId);
    LOG.debug("Checking out new local branch '{}' to track {}", checkoutBranch, change);
    changeRepository.checkoutAndTrackChange(checkoutBranch, numericId, targetBranch);
    userOutput.display(git.status());
    userOutput.display("Now tracking new change " + change);
  }

  private boolean confirm(BranchShortName targetBranch) {
    if (force) {
      return true;
    }
    return userInput.askYesNo(
        "This will create a change targeting branch "
            + targetBranch
            + ", reset the current branch "
            + git.getBranch()
            + " to the parent revision of the change and checkout the change to a new local branch."
            + "\nDo you want to continue?",
        false);
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
    public CheckoutNewCommand build(Boolean force, ChangeCheckoutBranchName checkoutBranch) {
      return new CheckoutNewCommand(
          gitProvider.build(),
          changeRepositoryFactory.build(),
          userInput,
          userOutput,
          force,
          checkoutBranch);
    }
  }
}
