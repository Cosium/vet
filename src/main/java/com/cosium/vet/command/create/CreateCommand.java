package com.cosium.vet.command.create;

import com.cosium.vet.command.VetCommand;
import com.cosium.vet.gerrit.GerritChange;
import com.cosium.vet.gerrit.GerritChangeRepository;
import com.cosium.vet.gerrit.GerritChangeRepositoryFactory;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;
import com.cosium.vet.runtime.UserInput;
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

  private final GerritChangeRepository changeRepository;
  private final UserInput userInput;

  private final boolean force;
  private final BranchShortName targetBranch;

  private CreateCommand(
      GerritChangeRepository changeRepository,
      UserInput userInput,
      // Optionals
      Boolean force,
      BranchShortName targetBranch) {
    this.changeRepository = changeRepository;
    this.userInput = requireNonNull(userInput);

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
    changeRepository.trackNewChange(targetBranch);
  }

  private boolean preserveCurrentChange() {
    if (force) {
      return false;
    }
    GerritChange gerritChange = changeRepository.getTrackedChange().orElse(null);
    if (gerritChange == null) {
      return false;
    }
    LOG.debug("Found current tracked change {}", gerritChange);
    return !userInput.askYesNo(
        "You are tracking change "
            + gerritChange
            + ". Are you sure that you want to stop tracking the current change and create a new one?",
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

    private final GerritChangeRepositoryFactory changeRepositoryFactory;
    private final UserInput userInput;

    public Factory(GerritChangeRepositoryFactory changeRepositoryFactory, UserInput userInput) {
      this.changeRepositoryFactory = requireNonNull(changeRepositoryFactory);
      this.userInput = requireNonNull(userInput);
    }

    @Override
    public CreateCommand build(Boolean force, BranchShortName targetBranch) {
      return new CreateCommand(changeRepositoryFactory.build(), userInput, force, targetBranch);
    }
  }
}
