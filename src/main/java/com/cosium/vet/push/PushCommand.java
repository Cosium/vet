package com.cosium.vet.push;

import com.cosium.vet.command.VetCommand;
import com.cosium.vet.gerrit.GerritChange;
import com.cosium.vet.gerrit.GerritClient;
import com.cosium.vet.gerrit.GerritClientFactory;
import com.cosium.vet.gerrit.PatchSetSubject;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.GitClientFactory;
import com.cosium.vet.git.RemoteName;
import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;
import com.cosium.vet.runtime.UserInput;
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
  private final GerritClient gerrit;
  private final UserInput userInput;
  private final BranchShortName targetBranch;
  private final Boolean publishDraftedComments;
  private final Boolean workInProgress;
  private final PatchSetSubject patchSetSubject;
  private final Boolean bypassReview;

  private PushCommand(
      GitClient gitClient,
      GerritClient gerritClient,
      UserInput userInput,
      // Optionals
      BranchShortName targetBranch,
      Boolean publishDraftedComments,
      Boolean workInProgress,
      PatchSetSubject patchSetSubject,
      Boolean bypassReview) {
    requireNonNull(gitClient);
    requireNonNull(gerritClient);
    requireNonNull(userInput);
    this.git = gitClient;
    this.gerrit = gerritClient;
    this.userInput = userInput;

    this.targetBranch = targetBranch;
    this.publishDraftedComments = publishDraftedComments;
    this.workInProgress = workInProgress;
    this.patchSetSubject = patchSetSubject;
    this.bypassReview = bypassReview;
  }

  @Override
  public void execute() {
    final GerritChange change;
    if (targetBranch != null) {
      LOG.debug("Target branch forced to '{}'", targetBranch);
      change = gerrit.setChange(targetBranch);
    } else {
      change = gerrit.getChange().orElseGet(this::askTargetBranchAndSetChange);
    }

    BranchShortName branch = change.getTargetBranch();
    RemoteName remote =
        git.getRemote(branch)
            .orElseThrow(
                () ->
                    new RuntimeException(String.format("No remote found for branch '%s'", branch)));
    git.fetch(remote, branch);
    String parent = git.getMostRecentCommonCommit(String.format("%s/%s", remote, branch));

    gerrit.createPatchSet(
        change,
        parent,
        git.getTree(),
        BooleanUtils.toBoolean(publishDraftedComments),
        BooleanUtils.toBoolean(workInProgress),
        patchSetSubject,
        BooleanUtils.toBoolean(bypassReview));
  }

  private GerritChange askTargetBranchAndSetChange() {
    BranchShortName targetBranch =
        BranchShortName.of(
            userInput.askNonBlank("Target branch", BranchShortName.MASTER.toString()));
    return gerrit.setChange(targetBranch);
  }

  public static class Factory implements PushCommandFactory {

    private final GitClientFactory gitClientFactory;
    private final GerritClientFactory gerritClientFactory;
    private final UserInput userInput;

    public Factory(
        GitClientFactory gitClientFactory,
        GerritClientFactory gerritClientFactory,
        UserInput userInput) {
      requireNonNull(gitClientFactory);
      requireNonNull(gerritClientFactory);
      requireNonNull(userInput);

      this.gitClientFactory = gitClientFactory;
      this.gerritClientFactory = gerritClientFactory;
      this.userInput = userInput;
    }

    @Override
    public PushCommand build(
        BranchShortName targetBranch,
        Boolean publishDraftedComments,
        Boolean workInProgress,
        PatchSetSubject patchSetSubject,
        Boolean bypassReview) {
      return new PushCommand(
          gitClientFactory.build(),
          gerritClientFactory.build(),
          userInput,
          targetBranch,
          publishDraftedComments,
          workInProgress,
          patchSetSubject,
          bypassReview);
    }
  }
}
