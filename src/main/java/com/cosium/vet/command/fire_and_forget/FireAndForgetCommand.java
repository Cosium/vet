package com.cosium.vet.command.fire_and_forget;

import com.cosium.vet.command.VetCommand;
import com.cosium.vet.gerrit.*;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.RevisionId;
import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;
import com.cosium.vet.runtime.UserInput;
import com.cosium.vet.runtime.UserOutput;
import com.cosium.vet.thirdparty.apache_commons_lang3.BooleanUtils;

import static java.util.Objects.requireNonNull;

/**
 * Created on 09/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class FireAndForgetCommand implements VetCommand<Change> {

  private static final Logger LOG = LoggerFactory.getLogger(FireAndForgetCommand.class);

  private final ChangeRepository changeRepository;
  private final GitClient git;
  private final UserInput userInput;
  private final UserOutput userOutput;

  private final boolean force;
  private final CodeReviewVote codeReviewVote;

  private FireAndForgetCommand(
      GitClient git,
      ChangeRepository changeRepository,
      UserInput userInput,
      UserOutput userOutput,
      // Optionals
      Boolean force,
      CodeReviewVote codeReviewVote) {
    this.changeRepository = requireNonNull(changeRepository);
    this.git = requireNonNull(git);
    this.userInput = requireNonNull(userInput);
    this.userOutput = requireNonNull(userOutput);

    this.force = BooleanUtils.toBoolean(force);
    this.codeReviewVote = codeReviewVote;
  }

  @Override
  public Change execute() {
    changeRepository
        .getTrackedChange()
        .ifPresent(
            change -> {
              throw new RuntimeException(
                  "Found current tracked change "
                      + change
                      + "."
                      + "\n'fire-and-forget' command can only be run when there is no tracked change.");
            });

    BranchShortName targetBranch = git.getBranch();
    if (!confirm(targetBranch)) {
      throw new RuntimeException("Answered no to the confirmation. Aborted.");
    }

    LOG.debug("Creating change targeting {}", targetBranch);
    PatchOptions patchOptions =
        PatchOptions.builder()
            .subject(PatchSubject.of("Fire and forget"))
            .codeReviewVote(codeReviewVote)
            .build();

    CreatedChange change = changeRepository.createChange(targetBranch, patchOptions);
    LOG.debug("Change {} created", change);
    userOutput.display(change.getCreationLog());

    RevisionId parent = change.fetchParent();
    LOG.debug("Resetting current branch to {}", parent);
    userOutput.display(git.resetKeep(parent));
    userOutput.display("Change " + change + " has been created.");

    return change;
  }

  private boolean confirm(BranchShortName targetBranch) {
    if (force) {
      return true;
    }
    return userInput.askYesNo(
        "This will create a change targeting branch "
            + targetBranch
            + " and reset the current branch "
            + git.getBranch()
            + " to the parent revision of the change"
            + ".\nDo you want to continue?",
        false);
  }

  public static class Factory implements FireAndForgetCommandFactory {

    private final ChangeRepositoryFactory changeRepositoryFactory;
    private final GitClient git;
    private final UserInput userInput;
    private final UserOutput userOutput;

    public Factory(
        GitClient git,
        ChangeRepositoryFactory changeRepositoryFactory,
        UserInput userInput,
        UserOutput userOutput) {
      this.changeRepositoryFactory = requireNonNull(changeRepositoryFactory);
      this.git = requireNonNull(git);
      this.userInput = requireNonNull(userInput);
      this.userOutput = requireNonNull(userOutput);
    }

    @Override
    public FireAndForgetCommand build(Boolean force, CodeReviewVote codeReviewVote) {
      return new FireAndForgetCommand(
          git, changeRepositoryFactory.build(), userInput, userOutput, force, codeReviewVote);
    }
  }
}
