/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.ibmmq.admin;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import za.co.absa.ibmmq.admin.commands.Command;
import za.co.absa.ibmmq.admin.commands.CreateQueueCommand;
import za.co.absa.ibmmq.admin.commands.CreateTopicCommand;
import za.co.absa.ibmmq.admin.commands.InquireQueueCommand;
import za.co.absa.ibmmq.admin.commands.InquireTopicCommand;

import com.ibm.mq.MQQueue;
import com.ibm.mq.MQTopic;

/**
 * Unit tests for the IBM MQ Admin Class.
 *
 * @author ahmed.seddiq
 * @version 1.0
 */
public class IbmMqAdminTest {

    private static final String QUEUE_NAME_EXISTS = "Q1";
    private static final String QUEUE_NAME_NOT_EXISTS = "Q2";
    private static final String TOPIC_NAME_EXISTS = "T1";
    private static final String TOPIC_NAME_NOT_EXISTS = "T2";

    @InjectMocks
    private IbmMqAdmin admin;

    @Mock
    private Command<MQQueue, InquireQueueCommand.Params> inquireQueueCommand;

    @Mock
    private Command<MQQueue, CreateQueueCommand.Params> createQueueCommand;

    @Mock
    private Command<MQTopic, InquireTopicCommand.Params> inquireTopicCommand;

    @Mock
    private Command<MQTopic, CreateTopicCommand.Params> createTopicCommand;

    @Mock
    private InquireQueueCommand.Params newInquireQueueCommandParams;

    @Mock
    private CreateQueueCommand.Params newCreateQueueCommandParams;

    @Mock
    private InquireTopicCommand.Params newInquireTopicCommandParams;

    @Mock
    private CreateTopicCommand.Params newCreateTopicCommandParams;

    @Mock
    private MQQueue existsQueue;

    @Mock
    private MQQueue newQueue;

    @Mock
    private MQTopic existsTopic;

    @Mock
    private MQTopic newTopic;

    /**
     * Init required mocks
     *
     * @throws Exception into JUnit
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(inquireQueueCommand.execute(mockInquiryQueueCommandParams(QUEUE_NAME_EXISTS))).thenReturn(existsQueue);
        when(inquireQueueCommand.execute(mockInquiryQueueCommandParams(QUEUE_NAME_NOT_EXISTS))).thenReturn(null);
        when(createQueueCommand.execute(mockCreateQueueCommandParams(QUEUE_NAME_NOT_EXISTS))).thenReturn(newQueue);

        when(inquireTopicCommand.execute(mockInquiryTopicCommandParams(TOPIC_NAME_EXISTS))).thenReturn(existsTopic);
        when(inquireTopicCommand.execute(mockInquiryTopicCommandParams(TOPIC_NAME_NOT_EXISTS))).thenReturn(null);
        when(createTopicCommand.execute(mockCreateTopicCommandParams(TOPIC_NAME_NOT_EXISTS))).thenReturn(newTopic);

        when(inquireQueueCommand.newParams()).thenReturn(newInquireQueueCommandParams);
        when(inquireTopicCommand.newParams()).thenReturn(newInquireTopicCommandParams);
        when(createQueueCommand.newParams()).thenReturn(newCreateQueueCommandParams);
        when(createTopicCommand.newParams()).thenReturn(newCreateTopicCommandParams);
    }

    @Test
    public void testGetOrCreateQueue_exists() throws Exception {
        admin.getOrCreateQueue(QUEUE_NAME_EXISTS);
        verify(inquireQueueCommand).execute(argThat(hasProperty("queueName", equalTo(QUEUE_NAME_EXISTS))));
        verifyNoMoreInteractions(createQueueCommand);
    }

    @Test
    public void testGetOrCreateQueue_notExists() throws Exception {
        admin.getOrCreateQueue(QUEUE_NAME_NOT_EXISTS);
        verify(inquireQueueCommand).execute(argThat(hasProperty("queueName", equalTo(QUEUE_NAME_NOT_EXISTS))));
        verify(createQueueCommand).execute(argThat(hasProperty("queueName", equalTo(QUEUE_NAME_NOT_EXISTS))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetOrCreateQueue_nullName() throws Exception {
        admin.getOrCreateQueue(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetOrCreateQueue_emptyName() throws Exception {
        admin.getOrCreateQueue("   ");
    }

    @Test
    public void testGetOrCreateTopic_exists() throws Exception {
        admin.getOrCreateTopic(TOPIC_NAME_EXISTS);
        verify(inquireTopicCommand).execute(argThat(hasProperty("topicName", equalTo(TOPIC_NAME_EXISTS))));
        verifyNoMoreInteractions(createTopicCommand);

    }

    @Test
    public void testGetOrCreateTopic_notExists() throws Exception {
        admin.getOrCreateTopic(TOPIC_NAME_NOT_EXISTS);
        verify(inquireTopicCommand).execute(argThat(hasProperty("topicName", equalTo(TOPIC_NAME_NOT_EXISTS))));
        verify(createTopicCommand).execute(argThat(hasProperty("topicName", equalTo(TOPIC_NAME_NOT_EXISTS))));
    }

    private InquireQueueCommand.Params mockInquiryQueueCommandParams(final String queueName) {
        final InquireQueueCommand.Params params = mock(InquireQueueCommand.Params.class);
        when(newInquireQueueCommandParams.withQueueName(queueName)).thenReturn(params);
        when(params.getQueueName()).thenReturn(queueName);
        return params;
    }

    private CreateQueueCommand.Params mockCreateQueueCommandParams(final String queueName) {
        final CreateQueueCommand.Params params = mock(CreateQueueCommand.Params.class);
        when(newCreateQueueCommandParams.withQueueName(queueName)).thenReturn(params);
        when(params.getQueueName()).thenReturn(queueName);
        return params;
    }

    private InquireTopicCommand.Params mockInquiryTopicCommandParams(final String topicName) {
        final InquireTopicCommand.Params params = mock(InquireTopicCommand.Params.class);
        when(newInquireTopicCommandParams.withTopicName(topicName)).thenReturn(params);
        when(params.getTopicName()).thenReturn(topicName);
        return params;
    }

    private CreateTopicCommand.Params mockCreateTopicCommandParams(final String topicName) {
        final CreateTopicCommand.Params params = mock(CreateTopicCommand.Params.class);
        when(newCreateTopicCommandParams.withTopicName(topicName)).thenReturn(params);
        when(params.getTopicName()).thenReturn(topicName);
        return params;
    }

}
