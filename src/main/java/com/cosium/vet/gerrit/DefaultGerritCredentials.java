package com.cosium.vet.gerrit;

import com.cosium.vet.gerrit.config.GerritConfigurationRepository;
import com.cosium.vet.gerrit.config.GerritSiteAuthConfiguration;
import com.cosium.vet.runtime.UserInput;

import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * Created on 22/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class DefaultGerritCredentials implements GerritCredentials {
  private final GerritConfigurationRepository configurationRepository;
  private final UserInput userInput;
  private final GerritHttpRootUrl rootUrl;
  private final AtomicReference<GerritUser> user;
  private final AtomicReference<GerritPassword> password;

  DefaultGerritCredentials(
      GerritConfigurationRepository configurationRepository,
      UserInput userInput,
      GerritHttpRootUrl rootUrl,
      // Optional
      GerritUser user,
      GerritPassword password) {
    requireNonNull(configurationRepository);
    requireNonNull(userInput);
    requireNonNull(rootUrl);

    this.configurationRepository = configurationRepository;
    this.userInput = userInput;
    this.rootUrl = rootUrl;

    this.user = new AtomicReference<>(user);
    this.password = new AtomicReference<>(password);
  }

  @Override
  public void invalidate() {
    user.set(null);
    password.set(null);
    dropSiteAuthConfiguration();
  }

  @Override
  public GerritHttpRootUrl getHttpRootUrl() {
    return rootUrl;
  }

  @Override
  public GerritUser getUser() {
    return GerritUser.of(getOrCreateSiteAuthConfiguration().getHttpLogin());
  }

  @Override
  public GerritPassword getPassword() {
    return GerritPassword.of(getOrCreateSiteAuthConfiguration().getHttpPassword());
  }

  private GerritSiteAuthConfiguration getOrCreateSiteAuthConfiguration() {
    return configurationRepository.readAndWrite(
        conf ->
            conf.getSiteAuth(rootUrl)
                .orElseGet(() -> conf.setAndGetSiteAuth(rootUrl, fetchUser(), fetchPassword())));
  }

  private GerritUser fetchUser() {
    return ofNullable(user.get())
        .orElseGet(() -> GerritUser.of(userInput.askNonBlank("Gerrit login")));
  }

  private GerritPassword fetchPassword() {
    return ofNullable(password.get())
        .orElseGet(() -> GerritPassword.of(userInput.askNonBlank("Gerrit password")));
  }

  private void dropSiteAuthConfiguration() {
    configurationRepository.readAndWrite(
        conf -> {
          conf.dropSiteAuth(rootUrl);
          return null;
        });
  }
}
