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
import com.cosium.vet.runtime.UserInput;
import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;

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
  private final BranchShortName targetBranch;
  private final PatchSetSubject patchSetSubject;

  private PushCommand(
      GitClient gitClient,
      GerritClient gerritClient,
      UserInput userInput,
      // Optionals
      BranchShortName targetBranch,
      PatchSetSubject patchSetSubject) {
    requireNonNull(gitClient);
    requireNonNull(gerritClient);
    requireNonNull(userInput);
    this.git = gitClient;
    this.gerrit = gerritClient;
    this.userInput = userInput;
    this.targetBranch = targetBranch;
    this.patchSetSubject = patchSetSubject;
  }

  @Override
  public void execute() {
    GerritChange change = gerrit.getChange().orElseGet(this::setAndGetChange);

    BranchShortName branch = change.getTargetBranch();
    RemoteName remote =
        git.getRemote(branch)
            .orElseThrow(
                () ->
                    new RuntimeException(String.format("No remote found for branch '%s'", branch)));

    String parent = git.getMostRecentCommonCommit(String.format("%s/%s", remote, branch));

    PatchSetSubject subject = patchSetSubject;
    if (subject == null) {
      subject =
          ofNullable(userInput.ask("Title for patch set"))
              .filter(StringUtils::isNotBlank)
              .map(PatchSetSubject::of)
              .orElse(null);
    }

    gerrit.createPatchSet(change, parent, git.getTree(), subject);
  }

  private GerritChange setAndGetChange() {
    BranchShortName targetBranch =
        ofNullable(this.targetBranch)
            .orElseGet(
                () ->
                    BranchShortName.of(
                        userInput.askNonBlank("Target branch", BranchShortName.MASTER.toString())));
    return gerrit.setAndGetChange(targetBranch);
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
    public PushCommand build(BranchShortName targetBranch, PatchSetSubject patchSetSubject) {
      return new PushCommand(
          gitClientFactory.build(),
          gerritClientFactory.build(),
          userInput,
          targetBranch,
          patchSetSubject);
    }
  }
}
