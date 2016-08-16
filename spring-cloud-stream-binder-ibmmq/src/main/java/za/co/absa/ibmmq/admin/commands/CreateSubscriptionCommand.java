/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.ibmmq.admin.commands;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import za.co.absa.ibmmq.IbmMqException;

import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.pcf.PCFMessage;

/**
 * a PCF command to create a subscription.
 *
 * @author ahmed.seddiq
 * @version 1.0
 */
public class CreateSubscriptionCommand extends BasePcfCommand<Void, CreateSubscriptionCommand.Params> {

    /**
     * Will do nothing
     *
     * @param res
     *            the result PCFMessages
     * @param params
     *            the passed Params to the execute method.
     * @return nothing
     */
    @Override
    protected Void handleSuccessResponse(final PCFMessage[] res, final Params params) {
        return null;
    }

    /**
     * Will check for the "Already exists" error code, and will mark the call as successful in this case by returning
     * null.
     *
     * @param reason
     *            the error reason code.
     * @return nothing
     * @throws IbmMqException
     *             if any error occurred during operation.
     */
    @Override
    protected Void handleErrorResponse(final int reason) throws IbmMqException {
        switch (reason) {
        case MQConstants.MQRCCF_SUB_ALREADY_EXISTS:
            return null;
        default:
            return super.handleErrorResponse(reason);
        }
    }

    /**
     * Creates a new instance of the Params for this Command.
     *
     * @return a new instance of the Params for this Command.
     */
    @Override
    public Params newParams() {
        return new Params();
    }

    /**
     * The Params class for this command.
     */
    public static class Params implements BasePcfCommand.Params {

        /**
         * The topic name.
         */
        private String topicString;
        /**
         * The destination queue name.
         */
        private String destinationQueue;

        /**
         * The name for the subscription.
         */
        private String name;

        public Params() {

        }

        /**
         * Builder method to configure subscription name.
         *
         * @param name
         *            the subscription name.
         * @return "this" for chaining.
         */
        public Params withName(final String name) {
            this.name = name;
            return this;
        }

        /**
         * Builder method to configure topic name.
         *
         * @param topicString
         *            the topic name.
         * @return "this" for chaining.
         */
        public Params withTopicString(final String topicString) {
            this.topicString = topicString;
            return this;
        }

        /**
         * Builder method to configure destination queue name.
         *
         * @param destinationQueue
         *            the destination queue name.
         * @return "this" for chaining.
         */
        public Params withDestinationQueue(final String destinationQueue) {
            this.destinationQueue = destinationQueue;
            return this;
        }

        /**
         * Creates the PCFMessage for the Create Subscription Command.
         *
         * @return the PCFMessage fot the Create Subscription Command.
         */
        @Override
        public PCFMessage toRequest() {

            final PCFMessage request = new PCFMessage(MQConstants.MQCMD_CREATE_SUBSCRIPTION);
            request.addParameter(MQConstants.MQCACF_SUB_NAME, getName());
            request.addParameter(MQConstants.MQCA_TOPIC_STRING, getTopicString());
            request.addParameter(MQConstants.MQCACF_DESTINATION, getDestinationQueue());
            return request;
        }

        /**
         * Validates the parameters.
         *
         * @throws IllegalArgumentException
         *             if any parameter is null or empty.
         */
        @Override
        public void validate() throws IllegalArgumentException {
            Assert.hasText(name);
            Assert.hasText(topicString);
            Assert.hasText(destinationQueue);
        }

        /**
         * Get destinationQueue
         *
         * @return destinationQueue
         */
        public String getDestinationQueue() {
            return safeUpperCase(destinationQueue);
        }

        /**
         * Get topicString
         *
         * @return topicString
         */
        public String getTopicString() {
            return safeUpperCase(topicString);
        }

        /**
         * Get name
         *
         * @return name
         */
        public String getName() {
            return safeUpperCase(name);
        }
    }
}
