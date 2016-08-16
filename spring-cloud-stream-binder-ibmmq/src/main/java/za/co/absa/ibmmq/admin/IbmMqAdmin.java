/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.ibmmq.admin;

import java.util.Optional;

import org.springframework.util.Assert;

import za.co.absa.ibmmq.IbmMqException;
import za.co.absa.ibmmq.admin.commands.Command;
import za.co.absa.ibmmq.admin.commands.CreateQueueCommand;
import za.co.absa.ibmmq.admin.commands.CreateSubscriptionCommand;
import za.co.absa.ibmmq.admin.commands.CreateTopicCommand;
import za.co.absa.ibmmq.admin.commands.InquireQueueCommand;
import za.co.absa.ibmmq.admin.commands.InquireTopicCommand;

import com.ibm.mq.MQQueue;
import com.ibm.mq.MQTopic;

/**
 * A Facade for executing commands against IBM MQ to perform scenarios like get or create queue/topic.
 *
 * @author ahmed.seddiq
 * @version 1.0
 */
public class IbmMqAdmin {
    private Command<MQQueue, InquireQueueCommand.Params> inquireQueueCommand;

    private Command<MQQueue, CreateQueueCommand.Params> createQueueCommand;

    private Command<MQTopic, InquireTopicCommand.Params> inquireTopicCommand;

    private Command<MQTopic, CreateTopicCommand.Params> createTopicCommand;

    private Command<Void, CreateSubscriptionCommand.Params> createSubscriptionCommand;


    /**
     * Gets the MQQueue instance for the given queue name.
     *
     * @param queueName
     *            the queue name
     * @return the MQQueue instance or null if not found.
     * @throws IbmMqException
     *             if any error occurs during operation.
     */
    public MQQueue inquireQueue(final String queueName) throws IbmMqException {
        Assert.hasText(queueName);
        return inquireQueueCommand.execute(inquireQueueCommand.newParams().withQueueName(queueName));
    }

    /**
     * Gets the MQTopic instance for the given topic name.
     *
     * @param topicName
     *            the topic name.
     * @return the MQTopic instance or null if not found.
     * @throws IbmMqException
     *             if any error occurs during operation.
     */
    public MQTopic inquireTopic(final String topicName) throws IbmMqException {
        Assert.hasText(topicName);
        return inquireTopicCommand.execute(inquireTopicCommand.newParams().withTopicName(topicName));
    }

    /**
     * Gets the queue if exists or creates new one with the given name.
     *
     * @param queueName
     *            the queue name.
     * @return the found/created queue.
     * @throws IbmMqException
     *             wraps any IBM error.
     */
    public MQQueue getOrCreateQueue(final String queueName) throws IbmMqException {
        Assert.hasText(queueName);
        return Optional
                .ofNullable(inquireQueueCommand.execute(inquireQueueCommand.newParams().withQueueName(queueName)))
                .orElseGet(() -> {
                    try {
                        return createQueueCommand.execute(createQueueCommand.newParams().withQueueName(queueName));
                    } catch (final IbmMqException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    /**
     * Gets the topic if exists or creates new one with the given name.
     *
     * @param topicName
     *            the topic name.
     * @return the found/created topic
     * @throws IbmMqException
     *             wraps any IBM error.
     */
    public MQTopic getOrCreateTopic(final String topicName) throws IbmMqException {
        Assert.hasText(topicName);
        return Optional
                .ofNullable(inquireTopicCommand.execute(inquireTopicCommand.newParams().withTopicName(topicName)))
                .orElseGet(() -> {
                    try {
                        return createTopicCommand.execute(createTopicCommand.newParams().withTopicName(topicName));
                    } catch (final IbmMqException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    /**
     * Creates the given topic, queue if not exists, then creates a subscription for the topic using the queue.
     *
     * @param topicString
     *            the topic name
     * @param queueName
     *            the queue name
     * @return the create queue
     * @throws IbmMqException
     *             if any error occurs during operation.
     */
    public MQQueue getOrCreateDurableQueue(final String topicString, final String queueName) throws IbmMqException {
        Assert.hasText(topicString);
        Assert.hasText(queueName);

        getOrCreateTopic(topicString);
        final MQQueue queue = getOrCreateQueue(queueName);
        createSubscriptionCommand.execute(createSubscriptionCommand.newParams().withName(queueName)
                .withDestinationQueue(queueName).withTopicString(topicString));
        return queue;
    }

    /**
     * Get createQueueCommand
     *
     * @return createQueueCommand
     */
    public Command<MQQueue, CreateQueueCommand.Params> getCreateQueueCommand() {
        return createQueueCommand;
    }

    /**
     * Set createQueueCommand
     *
     * @param createQueueCommand
     *            the new value.
     */
    public void setCreateQueueCommand(final Command<MQQueue, CreateQueueCommand.Params> createQueueCommand) {
        this.createQueueCommand = createQueueCommand;
    }

    /**
     * Get createTopicCommand
     *
     * @return createTopicCommand
     */
    public Command<MQTopic, CreateTopicCommand.Params> getCreateTopicCommand() {
        return createTopicCommand;
    }

    /**
     * Set createTopicCommand
     *
     * @param createTopicCommand
     *            the new value.
     */
    public void setCreateTopicCommand(final Command<MQTopic, CreateTopicCommand.Params> createTopicCommand) {
        this.createTopicCommand = createTopicCommand;
    }

    /**
     * Get inquireTopicCommand
     *
     * @return inquireTopicCommand
     */
    public Command<MQTopic, InquireTopicCommand.Params> getInquireTopicCommand() {
        return inquireTopicCommand;
    }

    /**
     * Set inquireTopicCommand
     *
     * @param inquireTopicCommand
     *            the new value.
     */
    public void setInquireTopicCommand(final Command<MQTopic, InquireTopicCommand.Params> inquireTopicCommand) {
        this.inquireTopicCommand = inquireTopicCommand;
    }

    /**
     * Get createSubscriptionCommand
     *
     * @return createSubscriptionCommand
     */
    public Command<Void, CreateSubscriptionCommand.Params> getCreateSubscriptionCommand() {
        return createSubscriptionCommand;
    }

    /**
     * Set createSubscriptionCommand
     *
     * @param createSubscriptionCommand
     *            the new value.
     */
    public void setCreateSubscriptionCommand(
        final Command<Void, CreateSubscriptionCommand.Params> createSubscriptionCommand) {
        this.createSubscriptionCommand = createSubscriptionCommand;
    }

    /**
     * Get inquireQueueCommand
     *
     * @return inquireQueueCommand
     */
    public Command<MQQueue, InquireQueueCommand.Params> getInquireQueueCommand() {
        return inquireQueueCommand;
    }

    /**
     * Set inquireQueueCommand
     *
     * @param inquireQueueCommand
     *            the new value.
     */
    public void setInquireQueueCommand(final Command<MQQueue, InquireQueueCommand.Params> inquireQueueCommand) {
        this.inquireQueueCommand = inquireQueueCommand;
    }


}
