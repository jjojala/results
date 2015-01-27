/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.data;

public class DataAccessException extends Exception {

    protected static final long serialVersionUID = -12345L;

    public DataAccessException(final String message) {
        super(message);
    }

    public DataAccessException(final Throwable cause) {
        super(cause);
    }

    public DataAccessException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
