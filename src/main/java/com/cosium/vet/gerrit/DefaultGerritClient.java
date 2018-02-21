package com.cosium.vet.gerrit;

import com.cosium.vet.gerrit.config.GerritConfigurationRepository;
import com.cosium.vet.git.BranchShortName;
import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.common.ChangeInfo;
import com.google.gerrit.extensions.common.ChangeInput;
import com.google.gerrit.extensions.restapi.RestApiException;

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
  private final GerritPushUrl pushUrl;
  private final GerritProjectName project;

  DefaultGerritClient(
      GerritConfigurationRepository configurationRepository,
      GerritApi gerritApi,
      GerritPushUrl pushUrl,
      GerritProjectName project) {
    requireNonNull(configurationRepository);
    requireNonNull(gerritApi);
    requireNonNull(pushUrl);
    requireNonNull(project);
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
      return new GerritChange(pushUrl, change);
    } catch (RestApiException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public GerritChange createAndSetChange(BranchShortName targetBranch, ChangeSubject subject) {
    ChangeInput changeInput =
        new ChangeInput(project.value(), targetBranch.value(), subject.value());
    try {
      ChangeInfo change = gerritApi.changes().create(changeInput).get();
      return new GerritChange(pushUrl, change);
    } catch (RestApiException e) {
      throw new RuntimeException(e);
    }
  }
}
