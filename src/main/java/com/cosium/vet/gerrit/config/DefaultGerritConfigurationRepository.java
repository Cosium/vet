package com.cosium.vet.gerrit.config;

import com.cosium.vet.gerrit.ChangeNumericId;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitConfigRepository;
import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;
import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;
import com.cosium.vet.thirdparty.apache_commons_lang3.math.NumberUtils;

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

  private static final Logger LOG =
      LoggerFactory.getLogger(DefaultGerritConfigurationRepository.class);

  private static final String VET_TRACKED_CHANGE_NUMERIC_ID = "vet-tracked-change-numeric-id";
  private static final String VET_TRACKED_CHANGE_TARGET_BRANCH = "vet-tracked-change-target-branch";

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
    return new GitStoredConfig(
        gitConfigRepository.getCurrentBranchValue(VET_TRACKED_CHANGE_NUMERIC_ID),
        gitConfigRepository.getCurrentBranchValue(VET_TRACKED_CHANGE_TARGET_BRANCH));
  }

  @Override
  public <T> T readAndWrite(Function<GerritConfiguration, T> functor) {
    GitStoredConfig config = doRead();
    T result = functor.apply(config);
    doWrite(config);
    return result;
  }

  private void doWrite(GitStoredConfig config) {
    LOG.debug("Writing {}", config);
    gitConfigRepository.setCurrentBranchValue(
        VET_TRACKED_CHANGE_NUMERIC_ID, config.trackedChangeNumericId.get());
    gitConfigRepository.setCurrentBranchValue(
        VET_TRACKED_CHANGE_TARGET_BRANCH, config.trackedChangeTargetBranch.get());
  }

  /**
   * Created on 19/02/18.
   *
   * @author Reda.Housni-Alaoui
   */
  private class GitStoredConfig implements GerritConfiguration {

    private final AtomicReference<String> trackedChangeNumericId;
    private final AtomicReference<String> trackedChangeTargetBranch;

    private GitStoredConfig(String trackedChangeNumericId, String trackedChangeTargetBranch) {
      this.trackedChangeNumericId = new AtomicReference<>(trackedChangeNumericId);
      this.trackedChangeTargetBranch = new AtomicReference<>(trackedChangeTargetBranch);
    }

    @Override
    public Optional<ChangeNumericId> getTrackedChangeNumericId() {
      return ofNullable(trackedChangeNumericId.get())
          .filter(NumberUtils::isDigits)
          .map(Long::parseLong)
          .map(ChangeNumericId::of);
    }

    @Override
    public void setTrackedChangeNumericId(ChangeNumericId numericId) {
      trackedChangeNumericId.set(ofNullable(numericId).map(ChangeNumericId::toString).orElse(null));
    }

    @Override
    public Optional<BranchShortName> getTrackedChangeTargetBranch() {
      return ofNullable(trackedChangeTargetBranch.get())
          .filter(StringUtils::isNotBlank)
          .map(BranchShortName::of);
    }

    @Override
    public void setTrackedChangeTargetBranch(BranchShortName targetBranch) {
      trackedChangeTargetBranch.set(targetBranch.toString());
    }

    @Override
    public String toString() {
      final StringBuilder sb = new StringBuilder("GitStoredConfig{");
      sb.append("changeNumericId=").append(trackedChangeNumericId.get());
      sb.append('}');
      return sb.toString();
    }
  }
}
