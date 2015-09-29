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
    public void onCreate(Class<?> resourceType, Object resultingResource) {
        LOG.log(Level.INFO, "onCreate: {0}: {1}", new Object[] {
            resourceType.getName(), resultingResource });
        if (delegate_ != null)
            delegate_.onCreate(resourceType, resultingResource);
    }

    @Override
    public void onUpdate(Class<?> resourceType, Object resultingResource) {
        LOG.log(Level.INFO, "onUpdate: {0}: {1}:", new Object[] {
            resourceType.getName(), resultingResource });
        if (delegate_ != null)
            delegate_.onUpdate(resourceType, resultingResource);
    }

    @Override
    public void onRemove(Class<?> resourceType, Object removedResource) {
        LOG.log(Level.INFO, "onRemove: {0}: {1}:", new Object[] {
            resourceType.getName(), removedResource });
        if (delegate_ != null)
            delegate_.onRemove(resourceType, removedResource);
    }
    
}
