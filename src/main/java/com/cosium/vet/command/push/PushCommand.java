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
public class PushCommand implements VetCommand {

  private static final Logger LOG = LoggerFactory.getLogger(PushCommand.class);

  private final ChangeRepository changeRepository;
  private final UserOutput userOutput;

  private final Boolean publishDraftedComments;
  private final Boolean workInProgress;
  private final PatchSubject patchSetSubject;
  private final Boolean bypassReview;
  private final CodeReviewVote codeReviewVote;

  private PushCommand(
      ChangeRepository changeRepository,
      UserOutput userOutput,
      // Optionals
      Boolean publishDraftedComments,
      Boolean workInProgress,
      PatchSubject patchSetSubject,
      Boolean bypassReview,
      CodeReviewVote codeReviewVote) {
    this.changeRepository = requireNonNull(changeRepository);
    this.userOutput = requireNonNull(userOutput);

    this.publishDraftedComments = publishDraftedComments;
    this.workInProgress = workInProgress;
    this.patchSetSubject = patchSetSubject;
    this.bypassReview = bypassReview;
    this.codeReviewVote = codeReviewVote;
  }

  @Override
  public void execute() {
    Change change =
        changeRepository
            .getTrackedChange()
            .orElseThrow(
                () -> new RuntimeException("There is no currently tracked change. Aborting."));

    LOG.debug("Found tracked change {}", change);

    PatchOptions patchOptions =
        PatchOptions.builder()
            .publishDraftComments(BooleanUtils.toBoolean(publishDraftedComments))
            .workInProgress(BooleanUtils.toBoolean(workInProgress))
            .subject(patchSetSubject)
            .bypassReview(BooleanUtils.toBoolean(bypassReview))
            .codeReviewVote(codeReviewVote)
            .build();

    String output = change.createPatch(patchOptions);

    userOutput.display(output);
    userOutput.display("Pushed to " + change);
  }

  public static class Factory implements PushCommandFactory {

    private final ChangeRepositoryFactory gerritChangeRepositoryFactory;
    private final UserOutput userOutput;

    public Factory(ChangeRepositoryFactory gerritChangeRepositoryFactory, UserOutput userOutput) {
      this.gerritChangeRepositoryFactory = requireNonNull(gerritChangeRepositoryFactory);
      this.userOutput = requireNonNull(userOutput);
    }

    @Override
    public PushCommand build(
        Boolean publishDraftedComments,
        Boolean workInProgress,
        PatchSubject patchSetSubject,
        Boolean bypassReview,
        CodeReviewVote codeReviewVote) {
      return new PushCommand(
          gerritChangeRepositoryFactory.build(),
          userOutput,
          publishDraftedComments,
          workInProgress,
          patchSetSubject,
          bypassReview,
          codeReviewVote);
    }
  }
}
