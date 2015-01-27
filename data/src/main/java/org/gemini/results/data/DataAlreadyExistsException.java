/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi).
 */
package org.gemini.results.data;

public class DataAlreadyExistsException extends DataAccessException {

    protected static final long serialVersionUID = -123456L;

    public DataAlreadyExistsException(final String message) {
        super(message);
    }

    public DataAlreadyExistsException(final Throwable cause) {
        super(cause);
    }

    public DataAlreadyExistsException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
