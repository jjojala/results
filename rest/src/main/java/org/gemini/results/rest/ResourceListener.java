/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi)
 */
package org.gemini.results.rest;

/**
 * Interface for listening resource changes.
 */
public interface ResourceListener {

    /**
     * Invoked when a resource of type {@code resourceType} has
     * been successfully created.
     * 
     * @param resourceType the type of the resource created
     * @param resultingResource the end result of the creation.
     *   The run-time type of {@code resultingResource} is always
     *   assignable to object of type {@code resourceType}.
     */
    void onCreate(final Class<?> resourceType, final Object resultingResource);
    
    /**
     * Invoked when a resource has been successfully updated.
     * 
     * @param resourceType type of the resource being updated.
     * @param resultingResource the end result of the update. The run-time
     *   type of {@code resultingResource} is always assignable to object
     *   of type {@code resourceType}.
     */
    void onUpdate(final Class<?> resourceType, final Object resultingResource);
    
    /**
     * Invoked when a resource has been successfully removed.
     * 
     * @param resourceType the type of the resource being removed.
     * @param removedResource the resource being removed. The run-time
     *   type of {@code removedResource} is always assignable to object
     *   of type {@code resourceType}.
     */
    void onRemove(final Class<?> resourceType, final Object removedResource);
}
