package com.cosium.vet.push;

import com.cosium.vet.VetCommand;
import com.cosium.vet.gerrit.GerritClientFactory;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.GitClientFactory;

import static java.util.Objects.requireNonNull;

/**
 * Created on 14/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class PushCommand implements VetCommand {

  private final GitClientFactory gitRepositoryProvider;
  private final GerritClientFactory gerritClientFactory;
  private final String targetBranch;

  PushCommand(
      GitClientFactory gitRepositoryProvider,
      GerritClientFactory gerritClientFactory,
      String targetBranch) {
    requireNonNull(gitRepositoryProvider);
    requireNonNull(gerritClientFactory);
    this.gitRepositoryProvider = gitRepositoryProvider;
    this.gerritClientFactory = gerritClientFactory;
    this.targetBranch = targetBranch;
  }

  @Override
  public void execute() {
    GitClient gitRepository = gitRepositoryProvider.buildClient();

  }
}
