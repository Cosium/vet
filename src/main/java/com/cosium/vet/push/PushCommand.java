package com.cosium.vet.push;

import com.cosium.vet.command.VetCommand;
import com.cosium.vet.gerrit.GerritChange;
import com.cosium.vet.gerrit.GerritChangeRepository;
import com.cosium.vet.gerrit.GerritChangeRepositoryFactory;
import com.cosium.vet.gerrit.PatchSetSubject;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.GitClientFactory;
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

  private final GitClient git;
  private final GerritChangeRepository gerritChangeRepository;
  private final UserOutput userOutput;

  private final Boolean publishDraftedComments;
  private final Boolean workInProgress;
  private final PatchSetSubject patchSetSubject;
  private final Boolean bypassReview;

  private PushCommand(
      GitClient gitClient,
      GerritChangeRepository gerritChangeRepository,
      UserOutput userOutput,
      // Optionals
      Boolean publishDraftedComments,
      Boolean workInProgress,
      PatchSetSubject patchSetSubject,
      Boolean bypassReview) {
    this.git = requireNonNull(gitClient);
    this.gerritChangeRepository = requireNonNull(gerritChangeRepository);
    this.userOutput = requireNonNull(userOutput);

    this.publishDraftedComments = publishDraftedComments;
    this.workInProgress = workInProgress;
    this.patchSetSubject = patchSetSubject;
    this.bypassReview = bypassReview;
  }

  @Override
  public void execute() {
    GerritChange change = gerritChangeRepository.getTrackedChange().orElse(null);
    if (change == null) {
      LOG.debug("No tracked change found");
      userOutput.display(
          "There is no currently tracked change. Please use 'create' or 'track' command to track a change.");
      return;
    }

    LOG.debug("Found tracked change {}", change);

    change.createPatchSet(
        git.getTree(),
        BooleanUtils.toBoolean(publishDraftedComments),
        BooleanUtils.toBoolean(workInProgress),
        patchSetSubject,
        BooleanUtils.toBoolean(bypassReview));
  }

  public static class Factory implements PushCommandFactory {

    private final GitClientFactory gitClientFactory;
    private final GerritChangeRepositoryFactory gerritChangeRepositoryFactory;
    private final UserOutput userOutput;

    public Factory(
        GitClientFactory gitClientFactory,
        GerritChangeRepositoryFactory gerritChangeRepositoryFactory,
        UserOutput userOutput) {
      this.gitClientFactory = requireNonNull(gitClientFactory);
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
          gitClientFactory.build(),
          gerritChangeRepositoryFactory.build(),
          userOutput,
          publishDraftedComments,
          workInProgress,
          patchSetSubject,
          bypassReview);
    }
  }
}
