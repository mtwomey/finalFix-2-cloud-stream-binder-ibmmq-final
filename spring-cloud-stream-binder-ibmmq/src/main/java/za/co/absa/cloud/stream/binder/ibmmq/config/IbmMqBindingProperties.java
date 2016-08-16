/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.cloud.stream.binder.ibmmq.config;

/**
 * Custom binding properties for IBM MQ.
 *
 * @author ahmed.seddiq
 * @version 1.0
 */
public class IbmMqBindingProperties {

    /**
     * The consumer binding properties.
     */
    private IbmMqConsumerProperties consumer = new IbmMqConsumerProperties();

    /**
     * The producer binding properties.
     */
    private IbmMqProducerProperties producer = new IbmMqProducerProperties();

    /**
     * Get The producer binding properties.
     *
     * @return The producer binding properties.
     */
    public IbmMqProducerProperties getProducer() {
        return producer;
    }

    /**
     * Set The producer binding properties.
     *
     * @param producer
     *            the new value.
     */
    public void setProducer(final IbmMqProducerProperties producer) {
        this.producer = producer;
    }

    /**
     * Get The consumer binding properties.
     *
     * @return The consumer binding properties.
     */
    public IbmMqConsumerProperties getConsumer() {
        return consumer;
    }

    /**
     * Set The consumer binding properties.
     *
     * @param consumer
     *            the new value.
     */
    public void setConsumer(final IbmMqConsumerProperties consumer) {
        this.consumer = consumer;
    }
}
