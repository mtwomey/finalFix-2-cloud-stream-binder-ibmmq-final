/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.ibmmq.admin.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import za.co.absa.ibmmq.IbmMqException;

import com.ibm.mq.MQTopic;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.pcf.PCFMessage;

/**
 * a PCF Command to create a IBM MQ Topic and returns an instance of MQTopic for the created topic.
 *
 * @author ahmed.seddiq
 * @version 1.0
 */
public class CreateTopicCommand extends BasePcfCommand<MQTopic, CreateTopicCommand.Params> {

    private InquireTopicCommand inquireTopicCommand;

    /**
     * Will use the InquireTopicCommand to get instance of the MQTopic for the created topic.
     *
     * @param res
     *            theh result PCFMessages
     * @param params
     *            the passed params to the execute method.
     * @return the MQTopic for the created topic.
     * @throws IbmMqException
     *             if any error occurred during operation.
     */
    @Override
    protected MQTopic handleSuccessResponse(final PCFMessage[] res, final Params params) throws IbmMqException {
        return inquireTopicCommand.execute(inquireTopicCommand.newParams().withTopicName(params.getTopicName()));
    }

    /**
     * Creates new instance of the Params class for this command.
     *
     * @return a new instance of the Params class for this command.
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
        private String topicName;

        public Params() {
        }

        /**
         * Builder method to configure topic name.
         *
         * @param topicName
         *            the topic name
         * @return this for chaining.
         */
        public Params withTopicName(final String topicName) {
            this.topicName = topicName;
            return this;
        }

        /**
         * Creates the PCFMessage for the Create Topic Command.
         *
         * @return the PCFMessage for the Create Topic Command.
         */
        @Override
        public PCFMessage toRequest() {
            final PCFMessage request = new PCFMessage(MQConstants.MQCMD_CREATE_TOPIC);
            request.addParameter(MQConstants.MQCA_TOPIC_NAME, getTopicName());
            request.addParameter(MQConstants.MQCA_TOPIC_STRING, getTopicName());
            return request;
        }

        /**
         * Validates the parameters
         *
         * @throws IllegalArgumentException
         *             if the topic name is null or empty.
         */
        @Override
        public void validate() throws IllegalArgumentException {
            Assert.hasText(topicName);
        }

        /**
         * Get topicName
         *
         * @return topicName
         */
        public String getTopicName() {
            return safeUpperCase(topicName);
        }

    }

    /**
     * Set inquireTopicCommand
     *
     * @param inquireTopicCommand the new value.
     */
    public void setInquireTopicCommand(InquireTopicCommand inquireTopicCommand) {
        this.inquireTopicCommand = inquireTopicCommand;
    }

    /**
     * Get inquireTopicCommand
     *
     * @return inquireTopicCommand
     */
    public InquireTopicCommand getInquireTopicCommand() {
        return inquireTopicCommand;
    }
}
