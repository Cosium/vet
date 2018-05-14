package com.cosium.vet.command.pull;

import com.cosium.vet.command.VetCommand;
import com.cosium.vet.gerrit.ChangeRepository;
import com.cosium.vet.runtime.UserOutput;

import static java.util.Objects.requireNonNull;

/**
 * Created on 09/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class PullCommand implements VetCommand {

  private final ChangeRepository changeRepository;
  private final UserOutput userOutput;

  private PullCommand(ChangeRepository changeRepository, UserOutput userOutput) {
    this.changeRepository = changeRepository;
    this.userOutput = requireNonNull(userOutput);
  }

  @Override
  public void execute() {
    userOutput.display(changeRepository.pull());
  }

  public static class Factory implements PullCommandFactory {

    private final ChangeRepository changeRepository;
    private final UserOutput userOutput;

    public Factory(ChangeRepository changeRepository, UserOutput userOutput) {
      this.changeRepository = requireNonNull(changeRepository);
      this.userOutput = requireNonNull(userOutput);
    }

    @Override
    public PullCommand build() {
      return new PullCommand(changeRepository, userOutput);
    }
  }
}
