/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.data;

public class DataNotFoundException extends DataAccessException {

    protected static final long serialVersionUID = -12345678L;

    public DataNotFoundException(final String message) {
        super(message);
    }

    public DataNotFoundException(final Throwable cause) {
        super(cause);
    }

    public DataNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
