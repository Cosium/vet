package com.cosium.vet.command.push;

import com.cosium.vet.command.VetCommand;
import com.cosium.vet.gerrit.Change;
import com.cosium.vet.gerrit.ChangeRepository;
import com.cosium.vet.gerrit.ChangeRepositoryFactory;
import com.cosium.vet.gerrit.PatchSetSubject;
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

  private final ChangeRepository gerritChangeRepository;
  private final UserOutput userOutput;

  private final Boolean publishDraftedComments;
  private final Boolean workInProgress;
  private final PatchSetSubject patchSetSubject;
  private final Boolean bypassReview;

  private PushCommand(
      ChangeRepository gerritChangeRepository,
      UserOutput userOutput,
      // Optionals
      Boolean publishDraftedComments,
      Boolean workInProgress,
      PatchSetSubject patchSetSubject,
      Boolean bypassReview) {
    this.gerritChangeRepository = requireNonNull(gerritChangeRepository);
    this.userOutput = requireNonNull(userOutput);

    this.publishDraftedComments = publishDraftedComments;
    this.workInProgress = workInProgress;
    this.patchSetSubject = patchSetSubject;
    this.bypassReview = bypassReview;
  }

  @Override
  public void execute() {
    Change change = gerritChangeRepository.getTrackedChange().orElse(null);
    if (change == null) {
      LOG.debug("No tracked change found");
      userOutput.display(
          "There is no currently tracked change.\n"
              + "Use 'create' or 'track' command to start tracking a change.");
      return;
    }

    LOG.debug("Found tracked change {}", change);

    change.createPatchSet(
        BooleanUtils.toBoolean(publishDraftedComments),
        BooleanUtils.toBoolean(workInProgress),
        patchSetSubject,
        BooleanUtils.toBoolean(bypassReview));
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
        PatchSetSubject patchSetSubject,
        Boolean bypassReview) {
      return new PushCommand(
          gerritChangeRepositoryFactory.build(),
          userOutput,
          publishDraftedComments,
          workInProgress,
          patchSetSubject,
          bypassReview);
    }
  }
}
