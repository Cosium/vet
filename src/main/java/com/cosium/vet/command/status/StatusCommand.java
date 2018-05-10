package com.cosium.vet.command.status;

import com.cosium.vet.command.VetCommand;
import com.cosium.vet.gerrit.Change;
import com.cosium.vet.gerrit.ChangeRepository;
import com.cosium.vet.gerrit.ChangeRepositoryFactory;
import com.cosium.vet.runtime.UserOutput;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Created on 09/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class StatusCommand implements VetCommand {

  private final ChangeRepository changeRepository;
  private final UserOutput userOutput;

  private StatusCommand(ChangeRepository changeRepository, UserOutput userOutput) {
    this.changeRepository = requireNonNull(changeRepository);
    this.userOutput = requireNonNull(userOutput);
  }

  @Override
  public void execute() {
    Optional<Change> change = changeRepository.getTrackedChange();
    if (change.isPresent()) {
      userOutput.display("Tracking change " + change.get() + ".");
    } else {
      userOutput.display("No tracked change.");
    }
  }

  public static class Factory implements StatusCommandFactory {

    private final ChangeRepositoryFactory changeRepositoryFactory;
    private final UserOutput userOutput;

    public Factory(ChangeRepositoryFactory changeRepositoryFactory, UserOutput userOutput) {
      this.changeRepositoryFactory = requireNonNull(changeRepositoryFactory);
      this.userOutput = requireNonNull(userOutput);
    }

    @Override
    public StatusCommand build() {
      return new StatusCommand(changeRepositoryFactory.build(), userOutput);
    }
  }
}
