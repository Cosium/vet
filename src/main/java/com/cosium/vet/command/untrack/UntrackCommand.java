package com.cosium.vet.command.untrack;

import com.cosium.vet.command.VetCommand;
import com.cosium.vet.gerrit.Change;
import com.cosium.vet.gerrit.ChangeRepository;
import com.cosium.vet.gerrit.ChangeRepositoryFactory;
import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;
import com.cosium.vet.runtime.UserInput;
import com.cosium.vet.thirdparty.apache_commons_lang3.BooleanUtils;

import static java.util.Objects.requireNonNull;

/**
 * Created on 09/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class UntrackCommand implements VetCommand<Void> {

  private static final Logger LOG = LoggerFactory.getLogger(UntrackCommand.class);

  private final ChangeRepository changeRepository;
  private final UserInput userInput;

  private final boolean force;

  private UntrackCommand(
      ChangeRepository changeRepository,
      UserInput userInput,
      // Optionals
      Boolean force) {
    this.changeRepository = changeRepository;
    this.userInput = requireNonNull(userInput);

    this.force = BooleanUtils.toBoolean(force);
  }

  @Override
  public Void execute() {
    if (preserveCurrentChange()) {
      throw new RuntimeException("Answered no to the confirmation. Aborted.");
    }
    changeRepository.untrack();
    return null;
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
            + ".\nAre you sure that you want to stop tracking this change?",
        false);
  }

  public static class Factory implements UntrackCommandFactory {

    private final ChangeRepositoryFactory changeRepositoryFactory;
    private final UserInput userInput;

    public Factory(ChangeRepositoryFactory changeRepositoryFactory, UserInput userInput) {
      this.changeRepositoryFactory = requireNonNull(changeRepositoryFactory);
      this.userInput = requireNonNull(userInput);
    }

    @Override
    public UntrackCommand build(Boolean force) {
      return new UntrackCommand(changeRepositoryFactory.build(), userInput, force);
    }
  }
}
