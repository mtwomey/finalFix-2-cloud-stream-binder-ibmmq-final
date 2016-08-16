/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.ibmmq.admin.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import za.co.absa.ibmmq.IbmMqException;
import za.co.absa.ibmmq.admin.IbmMqNativeClient;

import com.ibm.mq.MQException;
import com.ibm.mq.MQQueue;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;

/**
 * A Simple Command That will retrieve an instance of the configure queue if exist or null otherwise.
 *
 * @author ahmed.seddiq
 * @version 1.0
 */
public class InquireQueueCommand implements Command<MQQueue, InquireQueueCommand.Params> {
    private IbmMqNativeClient ibmMqNativeClient;

    /**
     * Using the QueueManager to retrieve an instance of the MQQueue for the passed queue name.
     *
     * @param params
     *            the parameters of this command.
     * @return an instance of MQQueue for the passed queue name.
     * @throws IbmMqException
     *             if any error occurred during operation.
     */
    public MQQueue execute(final Params params) throws IbmMqException {
        params.validate();

        try {
            return ibmMqNativeClient.getQueueManager().accessQueue(params.getQueueName(), MQConstants.MQOO_INQUIRE);
        } catch (final MQException e) {
            switch (e.getReason()) {
            case CMQC.MQRC_UNKNOWN_OBJECT_NAME:
                return null;
            default:
                throw new IbmMqException(String.format("Error opening queue (%s)", params.getQueueName()), e);
            }
        }
    }



    /**
     * Creates a new Params instance for this command.
     *
     * @return a new Params instance for this command.
     */
    @Override
    public Params newParams() {
        return new Params();
    }

    /**
     * The Params class for this Command.
     */
    public static class Params implements Command.Params {
        private String queueName;

        public Params() {
        }

        /**
         * Builder method to configure queue name.
         *
         * @param queueName
         *            the queue name.
         * @return "this" for chaining.
         */
        public Params withQueueName(final String queueName) {
            this.queueName = queueName;
            return this;
        }

        /**
         * Validates paramters
         *
         * @throws IllegalArgumentException
         *             if queue name is null or empty.
         */
        @Override
        public void validate() throws IllegalArgumentException {
            Assert.hasText(this.queueName);
        }

        /**
         * Get queueName
         *
         * @return queueName
         */
        public String getQueueName() {
            return safeUpperCase(queueName);
        }

    }

    /**
     * Set ibmMqNativeClient
     *
     * @param ibmMqNativeClient the new value.
     */
    public void setIbmMqNativeClient(IbmMqNativeClient ibmMqNativeClient) {
        this.ibmMqNativeClient = ibmMqNativeClient;
    }

    /**
     * Get ibmMqNativeClient
     *
     * @return ibmMqNativeClient
     */
    public IbmMqNativeClient getIbmMqNativeClient() {
        return ibmMqNativeClient;
    }
}
