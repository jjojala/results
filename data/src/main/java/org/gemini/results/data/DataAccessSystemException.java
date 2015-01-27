/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.data;

public class DataAccessSystemException extends DataAccessException {

    protected static final long serialVersionUID = -123456789L;

    public DataAccessSystemException(final String message) {
        super(message);
    }

    public DataAccessSystemException(final Throwable cause) {
        super(cause);
    }

    public DataAccessSystemException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
