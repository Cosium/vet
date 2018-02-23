package com.cosium.vet.gerrit.config;

import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitConfigRepository;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class DefaultGerritConfigurationRepository implements GerritConfigurationRepository {

  private static final String VET_CHANGE_TARGET_BRANCH = "vet-change-target-branch";

  private final GitConfigRepository gitConfigRepository;

  DefaultGerritConfigurationRepository(GitConfigRepository gitConfigRepository) {
    requireNonNull(gitConfigRepository);
    this.gitConfigRepository = gitConfigRepository;
  }

  @Override
  public GerritConfiguration read() {
    return doRead();
  }

  private GitStoredConfig doRead() {
    return new GitStoredConfig(gitConfigRepository.getCurrentBranchValue(VET_CHANGE_TARGET_BRANCH));
  }

  @Override
  public <T> T readAndWrite(Function<GerritConfiguration, T> functor) {
    GitStoredConfig config = doRead();
    T result = functor.apply(config);
    doWrite(config);
    return result;
  }

  private void doWrite(GitStoredConfig config) {
    gitConfigRepository.setCurrentBranchValue(
        VET_CHANGE_TARGET_BRANCH, config.changeTargetBranch.get());
  }

  /**
   * Created on 19/02/18.
   *
   * @author Reda.Housni-Alaoui
   */
  private class GitStoredConfig implements GerritConfiguration {

    private AtomicReference<String> changeTargetBranch;

    private GitStoredConfig(String changeTargetBranch) {
      this.changeTargetBranch = new AtomicReference<>(changeTargetBranch);
    }

    @Override
    public Optional<BranchShortName> getChangeTargetBranch() {
      return ofNullable(changeTargetBranch.get()).map(BranchShortName::of);
    }

    @Override
    public void setChangeTargetBranch(BranchShortName targetBranch) {
      changeTargetBranch.set(ofNullable(targetBranch).map(BranchShortName::toString).orElse(null));
    }
  }
}
