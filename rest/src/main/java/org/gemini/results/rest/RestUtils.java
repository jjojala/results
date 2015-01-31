/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.rest;

import java.net.URI;
import javax.ws.rs.core.Response;

public abstract class RestUtils {

    public static Response notFound() {
        return Response.status(Response.Status.NOT_FOUND).build();
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

    private RestUtils() { throw new AssertionError(); }
}
