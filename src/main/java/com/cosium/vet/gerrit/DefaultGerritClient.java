package com.cosium.vet.gerrit;

import com.cosium.vet.gerrit.config.GerritConfigurationRepository;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.GitUtils;
import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.common.ChangeInfo;
import com.google.gerrit.extensions.common.ChangeInput;
import com.google.gerrit.extensions.restapi.RestApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class DefaultGerritClient implements GerritClient {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultGerritClient.class);

  private final GitClient git;
  private final GerritConfigurationRepository configurationRepository;
  private final GerritApi gerritApi;
  private final GerritPushUrl pushUrl;
  private final GerritProjectName project;

  DefaultGerritClient(
      GitClient gitClient,
      GerritConfigurationRepository configurationRepository,
      GerritApi gerritApi,
      GerritPushUrl pushUrl,
      GerritProjectName project) {
    requireNonNull(gitClient);
    requireNonNull(configurationRepository);
    requireNonNull(gerritApi);
    requireNonNull(pushUrl);
    requireNonNull(project);
    this.git = gitClient;
    this.configurationRepository = configurationRepository;
    this.gerritApi = gerritApi;
    this.pushUrl = pushUrl;
    this.project = project;
  }

  @Override
  public Optional<GerritChange> getChange() {
    return configurationRepository.read().getChangeId().map(this::fetchChange);
  }

  private GerritChange fetchChange(ChangeId changeId) {
    try {
      ChangeInfo change = gerritApi.changes().id(changeId.value()).get();
      GerritChange gerritChange = new GerritChange(pushUrl, change);
      LOG.debug("Fetched change {}", gerritChange);
      return gerritChange;
    } catch (RestApiException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public GerritChange createAndSetChange(BranchShortName targetBranch, ChangeSubject subject) {
    ChangeInput changeInput =
        new ChangeInput(project.value(), targetBranch.value(), subject.value());
    changeInput.workInProgress = true;
    try {
      ChangeInfo change = gerritApi.changes().create(changeInput).get();
      configurationRepository.readAndWrite(
          conf -> {
            conf.setChangeId(ChangeId.of(change.id));
            return null;
          });
      GerritChange gerritChange = new GerritChange(pushUrl, change);
      LOG.debug("Created change {}", gerritChange);
      return gerritChange;
    } catch (RestApiException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void createPatchSet(
          GerritChange change, String startRevision, String endRevision, String patchSetTitle) {
    String commitMessage =
        String.format("%s\n\nChange-Id: %s", change.getSubject(), change.getChangeId());
    String commitId = git.commitTree(git.getTree(), startRevision, commitMessage);

    git.push(
        change.getPushUrl().toString(),
        String.format(
            "%s:refs/for/%s%%m=%s",
            commitId, change.getBranch(), GitUtils.encodeForGitRef(patchSetTitle)));
  }
}
