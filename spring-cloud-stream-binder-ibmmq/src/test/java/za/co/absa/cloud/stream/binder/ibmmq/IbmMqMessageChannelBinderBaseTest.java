/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.cloud.stream.binder.ibmmq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.binder.Binder;
import org.springframework.cloud.stream.binder.BinderFactory;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.support.GenericMessage;

import za.co.absa.ibmmq.admin.IbmMqAdmin;

import com.ibm.mq.MQQueue;

/**
 * Base class for IBM MQ Binder tests.
 *
 * @author ahmed.seddiq
 * @version 1.0
 */
public abstract class IbmMqMessageChannelBinderBaseTest {

    private ConfigurableApplicationContext context;
    private IbmMqAdmin ibmMqAdmin;

    @Before
    public void setUp() throws Exception {
        context = runTestApp();
        ibmMqAdmin = context.getBean(IbmMqAdmin.class);
    }

    protected abstract ConfigurableApplicationContext runTestApp();

    @After
    public void tearDown() throws Exception {
        if (context != null) {
            context.close();
        }
    }

    protected void expectQueue(final String queueName) throws Exception {
        final MQQueue queue = ibmMqAdmin.inquireQueue(queueName);
        assertNotNull(String.format("Queue (%s) was not created!", queueName), queue);
    }

    protected void expectTopic(final String topicName) throws Exception {
        assertNotNull(ibmMqAdmin.inquireTopic(topicName));
    }

    protected void expectDurableQueue(final String queueName) throws Exception {
        assertNotNull(ibmMqAdmin.inquireQueue(queueName));
    }

    protected void checkBinderCorrectlyPicked() {
        final BinderFactory<?> binderFactory = context.getBean(BinderFactory.class);
        final Binder binder = binderFactory.getBinder(null);
        assertThat(binder).isInstanceOf(IbmMqMessageChannelBinder.class);
    }

    /**
     * The sample producer app.
     */
    @SpringBootApplication
    @EnableBinding(Source.class)
    public static class SampleProducer {
        @Bean
        @InboundChannelAdapter(value = Source.OUTPUT)
        public MessageSource<String> produce() {
            return () -> new GenericMessage<>(new Date().toString());
        }
    }

    /**
     * The sample producer app.
     */
    @SpringBootApplication
    @EnableBinding(Sink.class)
    public static class SampleConsumer {
        @Bean
        @Input(value = Sink.INPUT)
        public void produce(final String message) {
        }
    }

}
