/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.demo.receiver;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.handler.annotation.SendTo;

@SpringBootApplication
@EnableBinding(Processor.class)
public class ToUpperStringTransformer {

    public static void main(final String[] args) {
        SpringApplication.run(ToUpperStringTransformer.class, args);
    }

    /**
     * The log4j logger.
     */
    private static final Logger LOG = Logger.getLogger(ToUpperStringTransformer.class);

    @StreamListener(Processor.INPUT)
    @SendTo(Processor.OUTPUT)
    public String toUpperString(final String str) {
        final String processed = str.toUpperCase();
        LOG.info("Upper: " + processed);
        return processed;
    }
}
