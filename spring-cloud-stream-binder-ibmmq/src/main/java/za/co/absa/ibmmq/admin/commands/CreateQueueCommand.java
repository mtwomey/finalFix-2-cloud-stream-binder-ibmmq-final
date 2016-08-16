/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.ibmmq.admin.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import za.co.absa.ibmmq.IbmMqException;
import za.co.absa.ibmmq.admin.IbmMqProperties;

import com.ibm.mq.MQQueue;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.pcf.PCFMessage;

/**
 * a PCF command that will creates a local IBM MQ Queue and returns an instance of MQQueue for the created queue.
 *
 * @author ahmed.seddiq
 * @version 1.0
 */
public class CreateQueueCommand extends BasePcfCommand<MQQueue, CreateQueueCommand.Params> {

    private IbmMqProperties ibmMqProperties;

    private InquireQueueCommand inquireQueueCommand;

    /**
     * Will use the InquireQueueCommand to retrieve an instance of the created Queue
     *
     * @param res
     *            the result messages
     * @param params
     *            the provided params for the execution.
     * @return the created MQQueue
     * @throws IbmMqException
     *             if any error occurred during operation.
     */
    @Override
    protected MQQueue handleSuccessResponse(final PCFMessage[] res, final Params params) throws IbmMqException {
        return inquireQueueCommand.execute(inquireQueueCommand.newParams().withQueueName(params.getQueueName()));
    }

    /**
     * Creates a new instance of the Params class.
     *
     * @return a new instance of the Params class.
     */
    @Override
    public Params newParams() {
        return new Params();
    }

    /**
     * The Params class for this command.
     */
    public class Params implements BasePcfCommand.Params {

        /**
         * The queue name.
         */
        private String queueName;

        /**
         * builder method to configure queue name.
         *
         * @param queueName
         *            the queue name
         * @return "this" for chaining.
         */
        public Params withQueueName(final String queueName) {
            this.queueName = queueName;
            return this;
        }

        /**
         * Creates the PCFMessage for the Create Queue Command.
         *
         * @return the PCFMessage for the Create Queue Command.
         */
        @Override
        public PCFMessage toRequest() {
            final PCFMessage request = new PCFMessage(MQConstants.MQCMD_CREATE_Q);
            request.addParameter(MQConstants.MQCA_Q_NAME, getQueueName());
            request.addParameter(MQConstants.MQIA_Q_TYPE, MQConstants.MQQT_LOCAL);
            request.addParameter(MQConstants.MQIA_MAX_Q_DEPTH, ibmMqProperties.getMaxQueueDepth());
            return request;
        }

        /**
         * Validates the parameters.
         *
         * @throws IllegalArgumentException
         *             if the queueName is null or empty.
         */
        @Override
        public void validate() throws IllegalArgumentException {
            Assert.hasText(queueName);
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
     * Get ibmMqProperties
     *
     * @return ibmMqProperties
     */
    public IbmMqProperties getIbmMqProperties() {
        return ibmMqProperties;
    }

    /**
     * Set ibmMqProperties
     *
     * @param ibmMqProperties the new value.
     */
    public void setIbmMqProperties(IbmMqProperties ibmMqProperties) {
        this.ibmMqProperties = ibmMqProperties;
    }

    /**
     * Set inquireQueueCommand
     *
     * @param inquireQueueCommand the new value.
     */
    public void setInquireQueueCommand(InquireQueueCommand inquireQueueCommand) {
        this.inquireQueueCommand = inquireQueueCommand;
    }

    /**
     * Get inquireQueueCommand
     *
     * @return inquireQueueCommand
     */
    public InquireQueueCommand getInquireQueueCommand() {
        return inquireQueueCommand;
    }
}
