/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.demo.sender;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.support.GenericMessage;

@SpringBootApplication
@EnableBinding(Source.class)
public class RandomStringGenerator {

    public static void main(final String[] args) {
        SpringApplication.run(RandomStringGenerator.class, args);
    }

    private static final Logger log = Logger.getLogger(RandomStringGenerator.class);

    private volatile int messageCounter = 0;

    @Bean
    @InboundChannelAdapter(value = Source.OUTPUT)
    public MessageSource<String> randomString() {
        return () -> {
            String random = "prefix" + "-00-" + RandomStringUtils.randomAlphabetic(3) + "-00-" + messageCounter++;
            log.info("Random: " + random);
            return new GenericMessage<>(random);
        };
    }
}
