/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.rest;

import javax.ws.rs.core.MediaType;

public abstract class RestUtils {

    public static final String[] MEDIATYPES = {
        MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML };

    private RestUtils() { throw new AssertionError(); }
}
