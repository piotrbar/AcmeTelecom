package com.acmetelecom.exceptions;

/**
 * This exception is thrown when an illegal call is trying to be initiated.
 * 
 */
public class IllegalCallException extends Exception {

    private static final long serialVersionUID = 1L;

    public IllegalCallException() {
    }

    public IllegalCallException(final String message) {
	super(message);
    }

    public IllegalCallException(final Throwable cause) {
	super(cause);
    }

    public IllegalCallException(final String message, final Throwable cause) {
	super(message, cause);
    }
}
