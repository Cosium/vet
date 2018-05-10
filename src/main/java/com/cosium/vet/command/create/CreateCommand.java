package com.cosium.vet.command.create;

import com.cosium.vet.command.VetCommand;
import com.cosium.vet.gerrit.Change;
import com.cosium.vet.gerrit.ChangeRepository;
import com.cosium.vet.gerrit.ChangeRepositoryFactory;
import com.cosium.vet.git.BranchShortName;
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
public class CreateCommand implements VetCommand {

  private static final Logger LOG = LoggerFactory.getLogger(CreateCommand.class);

  private final ChangeRepository changeRepository;
  private final UserInput userInput;
  private final UserOutput userOutput;

  private final boolean force;
  private final BranchShortName targetBranch;

  private CreateCommand(
      ChangeRepository changeRepository,
      UserInput userInput,
      UserOutput userOutput,
      // Optionals
      Boolean force,
      BranchShortName targetBranch) {
    this.changeRepository = changeRepository;
    this.userInput = requireNonNull(userInput);
    this.userOutput = requireNonNull(userOutput);

    this.force = BooleanUtils.toBoolean(force);
    this.targetBranch = targetBranch;
  }

  @Override
  public void execute() {
    if (preserveCurrentChange()) {
      return;
    }
    changeRepository.untrack();

    BranchShortName targetBranch = getTargetBranch();
    Change change = changeRepository.createAndTrackChange(targetBranch);

    userOutput.display("Now tracking new change " + change);
  }

  private boolean preserveCurrentChange() {
    if (force) {
      return false;
    }
    Change gerritChange = changeRepository.getTrackedChange().orElse(null);
    if (gerritChange == null) {
      return false;
    }
    LOG.debug("Found current tracked change {}", gerritChange);
    return !userInput.askYesNo(
        "You are tracking change "
            + gerritChange
            + ".\nAre you sure that you want to create and track a new one?",
        false);
  }

  private BranchShortName getTargetBranch() {
    return ofNullable(targetBranch)
        .orElseGet(
            () ->
                BranchShortName.of(
                    userInput.askNonBlank("Target branch", BranchShortName.MASTER.toString())));
  }

  public static class Factory implements CreateCommandFactory {

    private final ChangeRepositoryFactory changeRepositoryFactory;
    private final UserInput userInput;
    private final UserOutput userOutput;

    public Factory(
        ChangeRepositoryFactory changeRepositoryFactory,
        UserInput userInput,
        UserOutput userOutput) {
      this.changeRepositoryFactory = requireNonNull(changeRepositoryFactory);
      this.userInput = requireNonNull(userInput);
      this.userOutput = requireNonNull(userOutput);
    }

    @Override
    public CreateCommand build(Boolean force, BranchShortName targetBranch) {
      return new CreateCommand(
          changeRepositoryFactory.build(), userInput, userOutput, force, targetBranch);
    }
  }
}
