/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.cloud.stream.binder.ibmmq.config;

/**
 * Consumer binding properties for IBM MQ
 *
 * @author ahmed.seddiq
 * @version 1.0
 */
public class IbmMqConsumerProperties extends IbmMqBaseChannelProperties {

    /**
     * A custom transactionManager, can be used to plug e.g a custom XA transaction manager.
     */
    private String transactionManagerBean;

    /**
     * Get A custom transactionManager, can be used to plug e.g a custom XA transaction manager.
     *
     * @return A custom transactionManager, can be used to plug e.g a custom XA transaction manager.
     */
    public String getTransactionManagerBean() {
        return transactionManagerBean;
    }

    /**
     * Set A custom transactionManager, can be used to plug e.g a custom XA transaction manager.
     *
     * @param transactionManagerBean
     *            the new value.
     */
    public void setTransactionManagerBean(final String transactionManagerBean) {
        this.transactionManagerBean = transactionManagerBean;
    }
}
