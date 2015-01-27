/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.data;

public class DataConstraintViolationException extends DataAccessException {

    protected static final long serialVersionUID = -1234567L;

    public DataConstraintViolationException(final String message) {
        super(message);
    }

    public DataConstraintViolationException(final Throwable cause) {
        super(cause);
    }

    public DataConstraintViolationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
