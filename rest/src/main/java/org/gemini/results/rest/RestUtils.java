/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.rest;

import java.net.URI;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;

public abstract class RestUtils {

    private static final String ENTITY_EXISTS = 
            EntityExistsException.class.getSimpleName();
    private static final String ENTITY_NOT_FOUND =
            EntityNotFoundException.class.getSimpleName();

    public static Response notFound(
            final Class<?> entityType, final String key) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(String.format("%s: class=%s, key=%s",
                    ENTITY_NOT_FOUND, entityType.getName(), key)).build();
    }

    public static Response conflict(
            final Class<?> entityType, final String key) {
        return Response.status(Response.Status.CONFLICT)
                .entity(String.format("%s: class=%s, key=%s",
                    ENTITY_EXISTS, entityType.getName(), key)).build();
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
