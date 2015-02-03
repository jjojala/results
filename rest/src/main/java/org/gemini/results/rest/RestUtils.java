/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public abstract class RestUtils {

    public static Response notFound(final String message) {
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.TEXT_PLAIN).entity(message).build();
    }

    public static Response conflict() {
        return Response.status(Response.Status.CONFLICT).build();
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
