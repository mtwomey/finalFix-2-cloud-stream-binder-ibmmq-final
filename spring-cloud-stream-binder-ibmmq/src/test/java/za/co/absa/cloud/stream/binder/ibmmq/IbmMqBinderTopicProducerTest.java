/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.cloud.stream.binder.ibmmq;

import java.util.Date;

import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.support.GenericMessage;

/**
 * Test the binder with a producer application and with Topic destination.
 *
 * @author ahmed.seddiq
 * @version 1.0
 */
public class IbmMqBinderTopicProducerTest extends IbmMqMessageChannelBinderBaseTest {

    private final static String TEST_DESTINATION_OUTPUT = "test.destination.output";

    /**
     * Configure and run the test app
     *
     * @return the created application context
     */
    @Override
    protected ConfigurableApplicationContext runTestApp() {
        final String[] configProperties = {"--server.port=0",
            String.format("--spring.cloud.stream.bindings.output.destination=%s", TEST_DESTINATION_OUTPUT),
            "--spring.cloud.stream.ibmmq.binder.default-destination-type=topic" };

        return SpringApplication.run(SampleProducer.class, configProperties);
    }

    /**
     * Validates the expected resources was created by binder.
     *
     * @throws Exception
     *             into JUnit
     */
    @Test
    public void testProducer() throws Exception {
        checkBinderCorrectlyPicked();
        expectTopic(TEST_DESTINATION_OUTPUT);
    }


}
