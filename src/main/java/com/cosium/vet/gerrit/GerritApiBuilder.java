package com.cosium.vet.gerrit;

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

  private final GerritRestApiFactory gerritRestApiFactory;
  private final GerritCredentials gerritCredentials;

  GerritApiBuilder(GerritRestApiFactory gerritRestApiFactory, GerritCredentials gerritCredentials) {
    requireNonNull(gerritRestApiFactory);
    requireNonNull(gerritCredentials);
    this.gerritRestApiFactory = gerritRestApiFactory;
    this.gerritCredentials = gerritCredentials;
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
            "Gerrit authentication failed. Invalidating '{}' authentication configuration.",
            gerritCredentials.getHttpRootUrl());
        gerritCredentials.invalidate();
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

      GerritAuthData.Basic authData =
          new GerritAuthData.Basic(
              gerritCredentials.getHttpRootUrl().toString(),
              gerritCredentials.getUser().toString(),
              gerritCredentials.getPassword().toString());
      return gerritRestApiFactory.create(authData);
    }
  }
}
