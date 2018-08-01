package com.cosium.vet.command.push;

import com.cosium.vet.command.VetCommand;
import com.cosium.vet.gerrit.*;
import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;
import com.cosium.vet.runtime.UserOutput;
import com.cosium.vet.thirdparty.apache_commons_lang3.BooleanUtils;

import static java.util.Objects.requireNonNull;

/**
 * Created on 14/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class PushCommand implements VetCommand<Void> {

  private static final Logger LOG = LoggerFactory.getLogger(PushCommand.class);

  private final ChangeRepository changeRepository;
  private final UserOutput userOutput;

  private final Boolean publishDraftedComments;
  private final Boolean workInProgress;
  private final PatchsetSubject patchsetSubject;
  private final CodeReviewVote codeReviewVote;

  private PushCommand(
      ChangeRepository changeRepository,
      UserOutput userOutput,
      // Optionals
      Boolean publishDraftedComments,
      Boolean workInProgress,
      PatchsetSubject patchsetSubject,
      CodeReviewVote codeReviewVote) {
    this.changeRepository = requireNonNull(changeRepository);
    this.userOutput = requireNonNull(userOutput);

    this.publishDraftedComments = publishDraftedComments;
    this.workInProgress = workInProgress;
    this.patchsetSubject = patchsetSubject;
    this.codeReviewVote = codeReviewVote;
  }

  @Override
  public Void execute() {
    AlterableChange change =
        changeRepository
            .getTrackedChange()
            .orElseThrow(
                () -> new RuntimeException("There is no currently tracked change. Aborting."));

    LOG.debug("Found tracked change {}", change);

    PatchsetOptions patchOptions =
        PatchsetOptions.builder()
            .publishDraftComments(BooleanUtils.toBoolean(publishDraftedComments))
            .workInProgress(BooleanUtils.toBoolean(workInProgress))
            .subject(patchsetSubject)
            .codeReviewVote(codeReviewVote)
            .build();

    String output = change.createPatchset(patchOptions);

    userOutput.display(output);
    userOutput.display("Pushed to " + change);
    return null;
  }

  public static class Factory implements PushCommandFactory {

    private final ChangeRepositoryFactory changeRepositoryFactory;
    private final UserOutput userOutput;

    public Factory(ChangeRepositoryFactory changeRepositoryFactory, UserOutput userOutput) {
      this.changeRepositoryFactory = requireNonNull(changeRepositoryFactory);
      this.userOutput = requireNonNull(userOutput);
    }

    @Override
    public PushCommand build(
        Boolean publishDraftedComments,
        Boolean workInProgress,
        PatchsetSubject patchsetSubject,
        CodeReviewVote codeReviewVote) {
      return new PushCommand(
          changeRepositoryFactory.build(),
          userOutput,
          publishDraftedComments,
          workInProgress,
          patchsetSubject,
          codeReviewVote);
    }
  }
}
