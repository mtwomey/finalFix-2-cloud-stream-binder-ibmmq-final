/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.cloud.stream.binder.ibmmq;

import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Test the binder with a consumer application and with Topic destination. The consumer application will be configured
 * with a group.
 *
 * @author ahmed.seddiq
 * @version 1.0
 */
public class IbmMqBinderTopicConsumerTest extends IbmMqMessageChannelBinderBaseTest {

    private final static String TEST_DESTINATION_INPUT = "test.destination.input";
    private final static String TEST_DESTINATION_GROUP = "group1";

    /**
     * Configure and run the test app
     *
     * @return the created application context
     */
    @Override
    protected ConfigurableApplicationContext runTestApp() {
        final String[] configProperties = {"--server.port=0",
            String.format("--spring.cloud.stream.bindings.input.destination=%s", TEST_DESTINATION_INPUT),
            String.format("--spring.cloud.stream.bindings.input.group=%s", TEST_DESTINATION_GROUP),
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
    public void testConsumer() throws Exception {
        checkBinderCorrectlyPicked();
        expectTopic(TEST_DESTINATION_INPUT);
        expectDurableQueue(TEST_DESTINATION_INPUT + "." + TEST_DESTINATION_GROUP);
    }
}
