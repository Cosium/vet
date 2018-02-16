package com.cosium.vet.push;

import com.cosium.vet.VetCommand;
import com.cosium.vet.gerrit.GerritClientFactory;
import com.cosium.vet.git.GitRepository;
import com.cosium.vet.git.GitRepositoryProvider;

import static java.util.Objects.requireNonNull;

/**
 * Created on 14/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class PushCommand implements VetCommand {

  private final GitRepositoryProvider gitRepositoryProvider;
  private final GerritClientFactory gerritClientFactory;
  private final String targetBranch;

  PushCommand(
      GitRepositoryProvider gitRepositoryProvider,
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
    GitRepository gitRepository = gitRepositoryProvider.getRepository();

  }
}
