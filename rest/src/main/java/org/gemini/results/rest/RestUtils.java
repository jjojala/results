/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.rest;

import java.net.URI;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.ws.rs.core.Response;
import org.gemini.results.data.DataUtils;

public abstract class RestUtils {

    private static final String ENTITY_EXISTS = 
            EntityExistsException.class.getSimpleName();
    private static final String ENTITY_NOT_FOUND =
            EntityNotFoundException.class.getSimpleName();

    public static Response notFound(
            final Class<?> entityType, final String key) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(String.format("%s: %s", ENTITY_NOT_FOUND,
                    DataUtils.getIdentity(entityType, key))).build();
    }

    public static Response conflict(final PersistenceException ex) {
        return Response.status(Response.Status.CONFLICT)
                .entity(String.format("%s: %s",
                    ex.getClass().getSimpleName(), ex.getMessage())).build();
    }

    public static Response conflict(
            final Class<?> entityType, final String key) {
        return Response.status(Response.Status.CONFLICT)
                .entity(String.format("%s: %s", ENTITY_EXISTS,
                    DataUtils.getIdentity(entityType, key))).build();
    }

    public static Response ok() {
        return Response.ok().build();
    }

    public static Response ok(final Object entity) {
        return Response.ok(entity).build();
    }

    public static Response created(final URI location) {
        return Response.created(location).build();
    }

    private RestUtils() { throw new AssertionError(); }
}
