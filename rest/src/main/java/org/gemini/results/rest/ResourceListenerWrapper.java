/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple wrapper to a) log resource changes a b) prevent problems
 * when no listener have been given. This wrapper simply logs the
 * resource change and delegates the change to given wrapper (if any).
 */
public class ResourceListenerWrapper implements ResourceListener {

    private static final Logger LOG = Logger.getLogger(
            ResourceListenerWrapper.class.getName());
    
    private final ResourceListener delegate_;

    public ResourceListenerWrapper(final ResourceListener delegate) {
        delegate_ = delegate;
    }
    
    @Override
    public void onCreate(final Class<?> resourceType,
            final Object resultingResource, final Object resourceId) {
        LOG.log(Level.INFO, "onCreate: {0}: {1}", new Object[] {
            resourceType.getName(), resultingResource });
        if (delegate_ != null)
            delegate_.onCreate(resourceType, resultingResource, resourceId);
    }

    @Override
    public void onUpdate(final Class<?> resourceType,
            final Object resultingResource, final Object resourceId) {
        LOG.log(Level.INFO, "onUpdate: {0}: {1}:", new Object[] {
            resourceType.getName(), resultingResource });
        if (delegate_ != null)
            delegate_.onUpdate(resourceType, resultingResource, resourceId);
    }

    @Override
    public void onRemove(final Class<?> resourceType,
            final Object removedResource, final Object resourceId) {
        LOG.log(Level.INFO, "onRemove: {0}: {1}:", new Object[] {
            resourceType.getName(), removedResource });
        if (delegate_ != null)
            delegate_.onRemove(resourceType, removedResource, resourceId);
    }
}
