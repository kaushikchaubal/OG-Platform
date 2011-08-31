/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.language.view;

import java.util.Collection;
import java.util.Collections;

import com.opengamma.engine.view.client.ViewClient;
import com.opengamma.id.UniqueId;
import com.opengamma.language.context.SessionContext;
import com.opengamma.language.context.UserContext;

/**
 * Manages a set of {@link ViewClient} instances within a {@link SessionContext}. Each is uniquely identified by a {@link UniqueId}
 * and is also locked in the {@link UserContext} by its {@link ViewClientKey}. Typically a client that is referred to by a language
 * construct is "detached" from the user context into the session context. When the language construct releases the object, the
 * session reference is destroyed and the original user level lock released, possibly allowing the client to be destroyed.
 */
public class SessionViewClients extends ViewClients<UniqueId, SessionContext> {

  public SessionViewClients(final SessionContext context) {
    super(context);
  }

  /**
   * Gets the view client identified and increments the lock count. The client must already have been locked by a call to
   * {@link UserViewClients#lockViewClient}. Each call to lock a client must be balanced by a call to unlock it. Do not call after
   * (or while) calling {@link UserViewClients#destroyAll} or {@link SessionViewClients#destroyAll}.
   * 
   * @param uniqueId identifier of the view client
   * @return the client or null if the client is not already locked
   */
  @Override
  public DetachedViewClientHandle lockViewClient(final UniqueId uniqueId) {
    UserViewClient client;
    do {
      client = getClients().get(uniqueId);
      if (client == null) {
        return null;
      }
    } while (!client.incrementRefCount());
    return new DetachedViewClientHandle(this, client);
  }

  /**
   * Releases the detached reference, unlocking it in the user context. The client can no longer be referenced from the session context.
   * 
   * @param viewClient the client to unlock, not null
   */
  @Override
  protected void releaseViewClient(final UserViewClient viewClient) {
    if (getClients().remove(viewClient.getUniqueId()) != null) {
      if (!viewClient.decrementRefCount()) {
        getContext().getUserContext().getViewClients().releaseViewClient(viewClient);
      }
    } else {
      // This shouldn't happen
      throw new IllegalStateException();
    }
  }

  protected void addViewClient(final UserViewClient viewClient) {
    if (getClients().putIfAbsent(viewClient.getUniqueId(), viewClient) != null) {
      if (!viewClient.decrementRefCount()) {
        // This shouldn't happen
        throw new IllegalStateException();
      }
    }
  }

  /**
   * Returns the currently detached clients. Note that the clients are not locked by this operation, so may not be valid if they
   * have been unlocked by other threads.
   * 
   * @return the detached clients
   */
  public Collection<UserViewClient> getDetachedClients() {
    return Collections.unmodifiableCollection(getClients().values());
  }

}
