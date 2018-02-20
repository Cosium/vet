package com.cosium.vet.push;

import com.cosium.vet.VetCommand;
import com.cosium.vet.gerrit.ChangeId;
import com.cosium.vet.gerrit.GerritClient;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.runtime.UserInput;

import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * Created on 14/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class PushCommand implements VetCommand {

  private final GitClient git;
  private final GerritClient gerrit;
  private final UserInput userInput;
  private final RemoteName targetRemote;
  private final BranchShortName targetBranch;
  private final AtomicReference<ChangeDescription> changeDescription;

  public PushCommand(
      GitClient gitClient,
      GerritClient gerritClient,
      UserInput userInput,
      RemoteName targetRemote,
      BranchShortName targetBranch,
      ChangeDescription changeDescription) {
    requireNonNull(gitClient);
    requireNonNull(gerritClient);
    requireNonNull(userInput);
    this.git = gitClient;
    this.gerrit = gerritClient;
    this.userInput = userInput;
    this.targetRemote = ofNullable(targetRemote).orElse(RemoteName.ORIGIN);
    this.targetBranch = ofNullable(targetBranch).orElse(BranchShortName.MASTER);
    this.changeDescription = new AtomicReference<>(changeDescription);
  }

  @Override
  public void execute() {
    ChangeId changeId = gerrit.getChangeId().orElseGet(this::createChangeId);

    String parent =
        git.getMostRecentCommonCommit(
            String.format("%s/%s", targetRemote.value(), targetBranch.value()));

    String commitMessage = String.format("%s\n\nChange-Id: %s", changeDescription.get(), changeId);
    String commitId = git.commitTree(git.getTree(), parent, commitMessage);
    git.push(
        targetRemote.value(), String.format("%s:refs/for/refs/heads/%s", commitId, targetBranch));
  }

  private ChangeId createChangeId() {
    ChangeDescription desc =
        this.changeDescription.updateAndGet(
            currentDesc -> {
              if (currentDesc != null) {
                return currentDesc;
              }
              String lastCommitMessage = git.getLastCommitMessage();
              String rawDesc = userInput.ask("Change description", lastCommitMessage);
              return ChangeDescription.of(rawDesc);
            });
    return gerrit.createAndSetChangeId(desc.value());
  }
}
