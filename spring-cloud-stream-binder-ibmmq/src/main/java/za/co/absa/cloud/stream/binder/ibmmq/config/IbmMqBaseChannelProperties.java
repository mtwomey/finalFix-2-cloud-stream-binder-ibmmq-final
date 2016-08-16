/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.cloud.stream.binder.ibmmq.config;

import za.co.absa.cloud.stream.binder.ibmmq.DestinationType;

/**
 * A base class for both Consumer and Producer specific properties for IBM MQ
 *
 * @author ahmed.seddiq
 * @version 1.0
 */
public class IbmMqBaseChannelProperties {
    /**
     * The destination type for this channel.
     */
    private DestinationType destinationType;

    /**
     * Whether the channel is transacted.
     */
    private boolean transacted;

    /**
     * Get destinationType
     *
     * @return destinationType
     */
    public DestinationType getDestinationType() {
        return destinationType;
    }

    /**
     * Set destinationType
     *
     * @param destinationType
     *            the new value.
     */
    public void setDestinationType(final DestinationType destinationType) {
        this.destinationType = destinationType;
    }

    /**
     * Get transacted
     *
     * @return transacted
     */
    public boolean isTransacted() {
        return transacted;
    }

    /**
     * Set transacted
     *
     * @param transacted
     *            the new value.
     */
    public void setTransacted(final boolean transacted) {
        this.transacted = transacted;
    }
}
