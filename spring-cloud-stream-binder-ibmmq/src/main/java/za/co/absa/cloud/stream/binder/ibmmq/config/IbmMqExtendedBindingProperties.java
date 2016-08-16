/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.cloud.stream.binder.ibmmq.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.stream.binder.ExtendedBindingProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("spring.cloud.stream.ibmmq")
public class IbmMqExtendedBindingProperties
        implements ExtendedBindingProperties<IbmMqConsumerProperties, IbmMqProducerProperties> {

    private Map<String, IbmMqBindingProperties> bindings = new HashMap<>();

    public Map<String, IbmMqBindingProperties> getBindings() {
        return bindings;
    }

    public void setBindings(final Map<String, IbmMqBindingProperties> bindings) {
        this.bindings = bindings;
    }

    @Override
    public IbmMqConsumerProperties getExtendedConsumerProperties(final String channelName) {
        if (bindings.containsKey(channelName) && bindings.get(channelName).getConsumer() != null) {
            return bindings.get(channelName).getConsumer();
        } else {
            return new IbmMqConsumerProperties();
        }
    }

    @Override
    public IbmMqProducerProperties getExtendedProducerProperties(final String channelName) {
        if (bindings.containsKey(channelName) && bindings.get(channelName).getProducer() != null) {
            return bindings.get(channelName).getProducer();
        } else {
            return new IbmMqProducerProperties();
        }
    }
}
