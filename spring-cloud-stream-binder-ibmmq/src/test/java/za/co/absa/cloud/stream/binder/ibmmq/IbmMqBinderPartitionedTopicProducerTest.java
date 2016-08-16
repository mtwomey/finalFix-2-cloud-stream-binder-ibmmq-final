/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.cloud.stream.binder.ibmmq;

import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Test the binder with a producer application and with Topic destination and with partitioning
 *
 * @author ahmed.seddiq
 * @version 1.0
 */
public class IbmMqBinderPartitionedTopicProducerTest extends IbmMqMessageChannelBinderBaseTest {

    private final static String TEST_DESTINATION_OUTPUT = "test.destination.output";
    private final static Integer PARTITIONS_COUNT = 2;

    /**
     * Configure and run the test app
     *
     * @return the created application context
     */
    @Override
    protected ConfigurableApplicationContext runTestApp() {
        final String[] configProperties = {"--server.port=0",
            String.format("--spring.cloud.stream.bindings.output.destination=%s", TEST_DESTINATION_OUTPUT),
            "--spring.cloud.stream.bindings.output.producer.partitionKeyExpression=payload",
            String.format("--spring.cloud.stream.bindings.output.producer.partitionCount=%s", PARTITIONS_COUNT),
            "--spring.cloud.stream.bindings.output.producer.required-groups[0]=group1",
            "--spring.cloud.stream.bindings.output.producer.required-groups[1]=group2",
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
        // expect durable queue for each required group/partition combination.
        for (int p = 0; p < PARTITIONS_COUNT; p++) {
            expectDurableQueue(TEST_DESTINATION_OUTPUT + "." + p + "." + "group1");
            expectDurableQueue(TEST_DESTINATION_OUTPUT + "." + p + "." + "group2");
        }
    }

}
