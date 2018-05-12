package com.cosium.vet.command.fire_and_forget;

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
public class FireAndForgetCommand implements VetCommand {

  private static final Logger LOG = LoggerFactory.getLogger(FireAndForgetCommand.class);

  private final ChangeRepository changeRepository;
  private final GitClient git;
  private final UserInput userInput;
  private final UserOutput userOutput;

  private final boolean force;
  private final BranchShortName targetBranch;
  private final CodeReviewVote codeReviewVote;

  private FireAndForgetCommand(
      ChangeRepository changeRepository,
      GitClient git,
      UserInput userInput,
      UserOutput userOutput,
      // Optionals
      Boolean force,
      BranchShortName targetBranch,
      CodeReviewVote codeReviewVote) {
    this.changeRepository = requireNonNull(changeRepository);
    this.git = requireNonNull(git);
    this.userInput = requireNonNull(userInput);
    this.userOutput = requireNonNull(userOutput);

    this.force = BooleanUtils.toBoolean(force);
    this.targetBranch = targetBranch;
    this.codeReviewVote = codeReviewVote;
  }

  @Override
  public void execute() {
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

    if (!confirm()) {
      return;
    }

    BranchShortName targetBranch = getTargetBranch();
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
  }

  private boolean confirm() {
    if (force) {
      return true;
    }
    return userInput.askYesNo(
        "This will create a change and reset the current branch "
            + git.getBranch()
            + " to the parent revision of the change"
            + ".\nDo you want to continue?",
        false);
  }

  private BranchShortName getTargetBranch() {
    return ofNullable(targetBranch)
        .orElseGet(
            () ->
                BranchShortName.of(
                    userInput.askNonBlank("Target branch", BranchShortName.MASTER.toString())));
  }

  public static class Factory implements FireAndForgetCommandFactory {

    private final ChangeRepositoryFactory changeRepositoryFactory;
    private final GitProvider gitProvider;
    private final UserInput userInput;
    private final UserOutput userOutput;

    public Factory(
        ChangeRepositoryFactory changeRepositoryFactory,
        GitProvider gitProvider,
        UserInput userInput,
        UserOutput userOutput) {
      this.changeRepositoryFactory = requireNonNull(changeRepositoryFactory);
      this.gitProvider = requireNonNull(gitProvider);
      this.userInput = requireNonNull(userInput);
      this.userOutput = requireNonNull(userOutput);
    }

    @Override
    public FireAndForgetCommand build(
        Boolean force, BranchShortName targetBranch, CodeReviewVote codeReviewVote) {
      return new FireAndForgetCommand(
          changeRepositoryFactory.build(),
          gitProvider.build(),
          userInput,
          userOutput,
          force,
          targetBranch,
          codeReviewVote);
    }
  }
}
