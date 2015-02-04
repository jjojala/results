/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public abstract class RestUtils {

    private static final String ENTITY_EXISTS = 
            EntityExistsException.class.getSimpleName();
    private static final String ENTITY_NOT_FOUND =
            EntityNotFoundException.class.getSimpleName();

    @Deprecated
    public static Response notFound(final String message) {
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.TEXT_PLAIN).entity(message).build();
    }

    public static Response notFound(
            final Class<?> entityType, final String key) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(String.format("%s: class=%s, key=%s",
                    ENTITY_NOT_FOUND, entityType.getName(), key)).build();
    }

    @Deprecated
    public static Response conflict() {
        return Response.status(Response.Status.CONFLICT).build();
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

    public static Response serverError(final Throwable ex) {
        return Response.serverError().entity(toString(ex)).build();
    }

    public static String toString(final Throwable ex) {
        try {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ex.printStackTrace(new PrintStream(buffer));
            buffer.close();
            return buffer.toString();
        }

        catch (final IOException wrapped) {
            // Should not really happen for in-memory streams.
            throw new AssertionError(wrapped);
        }
    }

    private RestUtils() { throw new AssertionError(); }
}
