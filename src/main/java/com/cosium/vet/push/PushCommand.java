package com.cosium.vet.push;

import com.cosium.vet.VetCommand;
import com.cosium.vet.gerrit.ChangeSubject;
import com.cosium.vet.gerrit.GerritChange;
import com.cosium.vet.gerrit.GerritClient;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.GitUtils;
import com.cosium.vet.git.RemoteName;
import com.cosium.vet.runtime.UserInput;
import org.apache.commons.lang3.StringUtils;

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
  private final ChangeSubject changeSubject;
  private final BranchShortName targetBranch;

  public PushCommand(
      GitClient gitClient,
      GerritClient gerritClient,
      UserInput userInput,
      // Optionals
      ChangeSubject changeSubject,
      BranchShortName targetBranch) {
    requireNonNull(gitClient);
    requireNonNull(gerritClient);
    requireNonNull(userInput);
    this.git = gitClient;
    this.gerrit = gerritClient;
    this.userInput = userInput;
    this.changeSubject = changeSubject;
    this.targetBranch = ofNullable(targetBranch).orElse(BranchShortName.MASTER);
  }

  @Override
  public void execute() {
    String firstLineOfLastCommitMessage =
        StringUtils.substringBefore(git.getLastCommitMessage(), "\n");
    GerritChange change =
        gerrit.getChange().orElseGet(() -> createChange(firstLineOfLastCommitMessage));

    BranchShortName branch = change.getBranch();
    RemoteName remote =
        git.getRemote(branch)
            .orElseThrow(
                () ->
                    new RuntimeException(String.format("No remote found for branch '%s'", branch)));

    String parent = git.getMostRecentCommonCommit(String.format("%s/%s", remote, branch));

    String commitMessage =
        String.format("%s\n\nChange-Id: %s", change.getSubject(), change.getChangeId());
    String commitId = git.commitTree(git.getTree(), parent, commitMessage);

    String patchSetTitle =
        userInput.askNonBlank("Title for patch set", firstLineOfLastCommitMessage);
    git.push(
        change.getPushUrl().toString(),
        String.format(
            "%s:refs/for/%s%%m=%s",
            commitId, change.getBranch(), GitUtils.encodeForGitRef(patchSetTitle)));
  }

  private GerritChange createChange(String defaultSubject) {
    ChangeSubject subject =
        ofNullable(changeSubject)
            .orElseGet(
                () ->
                    ChangeSubject.of(
                        userInput.askNonBlank("Title for change set", defaultSubject)));
    return gerrit.createAndSetChange(targetBranch, subject);
  }
}
