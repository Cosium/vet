package com.cosium.vet.command.status;

import com.cosium.vet.command.VetCommand;
import com.cosium.vet.gerrit.AlterableChange;
import com.cosium.vet.gerrit.ChangeRepository;
import com.cosium.vet.gerrit.ChangeRepositoryFactory;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.runtime.UserOutput;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Created on 09/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class StatusCommand implements VetCommand<Void> {

  private final GitClient git;
  private final ChangeRepository changeRepository;
  private final UserOutput userOutput;

  private StatusCommand(GitClient git, ChangeRepository changeRepository, UserOutput userOutput) {
    this.git = requireNonNull(git);
    this.changeRepository = requireNonNull(changeRepository);
    this.userOutput = requireNonNull(userOutput);
  }

  @Override
  public Void execute() {
    Optional<AlterableChange> change = changeRepository.getTrackedChange();
    userOutput.display(git.status());
    if (change.isPresent()) {
      userOutput.display("Tracking change " + change.get());
    } else {
      userOutput.display("No tracked change.");
    }
    return null;
  }

  public static class Factory implements StatusCommandFactory {

    private final GitClient git;
    private final ChangeRepositoryFactory changeRepositoryFactory;
    private final UserOutput userOutput;

    public Factory(
        GitClient git, ChangeRepositoryFactory changeRepositoryFactory, UserOutput userOutput) {
      this.git = requireNonNull(git);
      this.changeRepositoryFactory = requireNonNull(changeRepositoryFactory);
      this.userOutput = requireNonNull(userOutput);
    }

    @Override
    public StatusCommand build() {
      return new StatusCommand(git, changeRepositoryFactory.build(), userOutput);
    }
  }
}
