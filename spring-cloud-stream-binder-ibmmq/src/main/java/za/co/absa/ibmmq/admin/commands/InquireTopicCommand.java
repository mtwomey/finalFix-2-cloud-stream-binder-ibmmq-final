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
import com.ibm.mq.MQTopic;
import com.ibm.mq.constants.MQConstants;

/**
 * A Simple Command That will retrieve an instance of the configure topic if exist or null otherwise.
 *
 * @author ahmed.seddiq
 * @version 1.0
 */
public class InquireTopicCommand implements Command<MQTopic, InquireTopicCommand.Params> {
    private IbmMqNativeClient ibmMqNativeClient;

    /**
     * Using the QueueManager to retrieve an instance of the MQTopic for the passed topic name.
     *
     * @param params
     *            the parameters of this command.
     * @return an instance of MQTopic for the passed topic name.
     * @throws IbmMqException
     *             if any error occurred during operation.
     */
    public MQTopic execute(final Params params) throws IbmMqException {
        params.validate();

        try {
            // The Destination name will be the topicname concatenated with topicobject, so using
            // empty string here for topicString and the given name as the topic object.
            return ibmMqNativeClient.getQueueManager().accessTopic("", params.getTopicName(),
                    MQConstants.MQTOPIC_OPEN_AS_PUBLICATION, MQConstants.MQOO_OUTPUT);
        } catch (final MQException e) {
            switch (e.getReason()) {
            case MQConstants.MQRC_UNKNOWN_OBJECT_NAME:
                return null;
            default:
                throw new IbmMqException(String.format("Error opening topic (%s)", params.getTopicName()), e);
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
        private String topicName;

        public Params() {
        }

        /**
         * Builder method to configure topic name.
         *
         * @param topicName
         *            the topic name.
         * @return "this" for chaining.
         */
        public Params withTopicName(final String topicName) {
            this.topicName = topicName;
            return this;
        }

        /**
         * Validates parameters
         *
         * @throws IllegalArgumentException
         *             if topic name is null or empty.
         */
        @Override
        public void validate() throws IllegalArgumentException {
            Assert.hasText(this.topicName);
        }

        /**
         * Get topicName
         *
         * @return topicName
         */
        public String getTopicName() {
            return topicName.toUpperCase();
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
