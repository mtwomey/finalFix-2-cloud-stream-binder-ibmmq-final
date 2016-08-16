/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.ibmmq;

/**
 * A general exception for IBM MQ errors.
 *
 * @author ahmed.seddiq
 * @version 1.0
 */
public class IbmMqException extends Exception {
    /**
     * Constructs instance from message.
     *
     * @param message
     *            the message
     */
    public IbmMqException(final String message) {
        super(message);
    }

    /**
     * constructs instance from message and cause
     *
     * @param message
     *            the message
     * @param e
     *            the cause
     */
    public IbmMqException(final String message, final Throwable e) {
        super(message, e);
    }
}
