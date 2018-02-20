package com.cosium.vet.gerrit;

import com.cosium.vet.gerrit.config.GerritConfiguration;
import com.cosium.vet.gerrit.config.GerritConfigurationRepository;
import com.cosium.vet.git.GitClient;
import com.google.gerrit.extensions.api.GerritApi;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class DefaultGerritClient implements GerritClient {

  private final GerritConfigurationRepository configurationRepository;
  private final GerritApi gerritApi;
  private final GitClient gitClient;

  DefaultGerritClient(
      GerritConfigurationRepository configurationRepository,
      GerritApi gerritApi,
      GitClient gitClient) {
    requireNonNull(configurationRepository);
    requireNonNull(gerritApi);
    requireNonNull(gitClient);
    this.configurationRepository = configurationRepository;
    this.gerritApi = gerritApi;
    this.gitClient = gitClient;
  }

  @Override
  public Optional<ChangeId> getChangeId() {
    return configurationRepository.read().getCurrentChangeId().map(ChangeId::of);
  }

  /** Works the same way as Gerrit msg commit hook but can be called on demand on all platforms. */
  @Override
  public ChangeId createAndSetChangeId(String changeDescription) {
    List<String> lines = new ArrayList<>();
    lines.add("tree " + gitClient.writeTree());
    lines.add("parent " + gitClient.revParse("HEAD~ 0"));
    lines.add("author " + gitClient.var("GIT_AUTHOR_IDENT"));
    lines.add("committer " + gitClient.var("GIT_COMMITTER_IDENT"));
    lines.add(StringUtils.EMPTY);
    lines.add(changeDescription);
    String changeId = "I" + gitClient.hashObject("commit", StringUtils.join(lines, "\n"));
    GerritConfiguration gerritConfiguration = configurationRepository.read();
    gerritConfiguration.setCurrentChangeId(changeId);
    return ChangeId.of(changeId);
  }
}
