package com.cosium.vet.command.untrack;

import com.cosium.vet.command.VetCommand;
import com.cosium.vet.gerrit.GerritChange;
import com.cosium.vet.gerrit.GerritChangeRepository;
import com.cosium.vet.gerrit.GerritChangeRepositoryFactory;
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
public class UntrackCommand implements VetCommand {

  private static final Logger LOG = LoggerFactory.getLogger(UntrackCommand.class);

  private final GerritChangeRepository changeRepository;
  private final UserInput userInput;

  private final boolean force;

  private UntrackCommand(
      GerritChangeRepository changeRepository,
      UserInput userInput,
      // Optionals
      Boolean force) {
    this.changeRepository = changeRepository;
    this.userInput = requireNonNull(userInput);

    this.force = BooleanUtils.toBoolean(force);
  }

  @Override
  public void execute() {
    if (preserveCurrentChange()) {
      return;
    }
    changeRepository.untrack();
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
            + ". Are you sure that you want to stop tracking the current change?",
        false);
  }

  public static class Factory implements UntrackCommandFactory {

    private final GerritChangeRepositoryFactory changeRepositoryFactory;
    private final UserInput userInput;

    public Factory(GerritChangeRepositoryFactory changeRepositoryFactory, UserInput userInput) {
      this.changeRepositoryFactory = requireNonNull(changeRepositoryFactory);
      this.userInput = requireNonNull(userInput);
    }

    @Override
    public UntrackCommand build(Boolean force) {
      return new UntrackCommand(changeRepositoryFactory.build(), userInput, force);
    }
  }
}
