package com.cosium.vet.gerrit;

import com.cosium.vet.gerrit.config.GerritConfigurationRepository;
import com.cosium.vet.gerrit.config.GerritSiteAuthConfiguration;
import com.cosium.vet.runtime.UserInput;
import com.google.gerrit.extensions.api.GerritApi;
import com.urswolfer.gerrit.client.rest.GerritAuthData;
import com.urswolfer.gerrit.client.rest.GerritRestApiFactory;
import com.urswolfer.gerrit.client.rest.http.HttpStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;

/**
 * Created on 21/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
class GerritApiBuilder {

  private final GerritConfigurationRepository configurationRepository;
  private final GerritRestApiFactory gerritRestApiFactory;
  private final UserInput userInput;
  private final GerritHttpRootUrl rootUrl;

  private final AtomicReference<GerritUser> user;
  private final AtomicReference<GerritPassword> password;

  GerritApiBuilder(
      GerritConfigurationRepository configurationRepository,
      GerritRestApiFactory gerritRestApiFactory,
      UserInput userInput,
      GerritHttpRootUrl rootUrl,
      // Optional
      GerritUser user,
      GerritPassword password) {
    requireNonNull(configurationRepository);
    requireNonNull(gerritRestApiFactory);
    requireNonNull(userInput);
    requireNonNull(rootUrl);
    this.configurationRepository = configurationRepository;
    this.gerritRestApiFactory = gerritRestApiFactory;
    this.userInput = userInput;
    this.rootUrl = rootUrl;

    this.user = new AtomicReference<>(user);
    this.password = new AtomicReference<>(password);
  }

  GerritApi build() {
    return (GerritApi)
        Proxy.newProxyInstance(
            getClass().getClassLoader(), new Class[] {GerritApi.class}, new Api());
  }

  private class Api implements InvocationHandler {

    private final Logger log = LoggerFactory.getLogger(Api.class);
    private final AtomicReference<GerritApi> delegate = new AtomicReference<>();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      GerritApi internalApi = delegate.updateAndGet(this::buildInternalIfNeeded);
      try {
        return method.invoke(internalApi, args);
      } catch (InvocationTargetException e) {
        Throwable cause = e.getCause();
        if (!(cause instanceof HttpStatusException)) {
          throw cause;
        }
        HttpStatusException httpStatusException = (HttpStatusException) cause;
        if (httpStatusException.getStatusCode() != 401) {
          throw cause;
        }
        log.debug(
            "Gerrit authentication failed. Dropping '{}' authentication configuration.", rootUrl);
        dropSiteAuthConfiguration();
        delegate.set(null);
        return invoke(proxy, method, args);
      }
    }

    /**
     * @param gerritApi The current internal api
     * @return The current internal api or a new one if needed
     */
    private GerritApi buildInternalIfNeeded(GerritApi gerritApi) {
      if (gerritApi != null) {
        return gerritApi;
      }

      GerritSiteAuthConfiguration authConf = getOrCreateSiteAuthConfiguration();
      GerritAuthData.Basic authData =
          new GerritAuthData.Basic(
              authConf.getHttpUrl(), authConf.getHttpLogin(), authConf.getHttpPassword());
      return gerritRestApiFactory.create(authData);
    }

    /** Drop the site auth configuration */
    private void dropSiteAuthConfiguration() {
      configurationRepository.readAndWrite(
          conf -> {
            conf.dropSiteAuth(rootUrl);
            return null;
          });
    }

    /** @return The created or found site configuration */
    private GerritSiteAuthConfiguration getOrCreateSiteAuthConfiguration() {
      return configurationRepository.readAndWrite(
          conf ->
              conf.getSiteAuth(rootUrl)
                  .orElseGet(() -> conf.setAndGetSiteAuth(rootUrl, fetchUser(), fetchPassword())));
    }

    /** @return The user passed as parameter or the user provided via user input */
    private GerritUser fetchUser() {
      GerritUser u = user.getAndSet(null);
      if (u != null) {
        return u;
      }
      return GerritUser.of(userInput.askNonBlank("Gerrit http login"));
    }

    /** @return The password passed as parameter or the user provided via user input */
    private GerritPassword fetchPassword() {
      GerritPassword p = password.getAndSet(null);
      if (p != null) {
        return p;
      }
      return GerritPassword.of(userInput.askNonBlank("Gerrit http password"));
    }
  }
}
