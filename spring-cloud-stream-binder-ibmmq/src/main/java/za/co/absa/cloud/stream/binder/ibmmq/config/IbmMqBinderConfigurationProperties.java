/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.cloud.stream.binder.ibmmq.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import za.co.absa.cloud.stream.binder.ibmmq.DestinationType;
import za.co.absa.ibmmq.admin.HeaderCompressionTechnique;
import za.co.absa.ibmmq.admin.MessageCompressionTechnique;

@ConfigurationProperties(prefix = "spring.cloud.stream.ibmmq.binder")
public class IbmMqBinderConfigurationProperties {
    private DestinationType defaultDestinationType = DestinationType.QUEUE;

    private List<MessageCompressionTechnique> msgCompList =
            new ArrayList<>(Collections.singleton(MessageCompressionTechnique.DEFAULT));

    private List<HeaderCompressionTechnique> hdrCompList =
            new ArrayList<>(Collections.singleton(HeaderCompressionTechnique.DEFAULT));

    /**
     * Get msgCompList
     *
     * @return msgCompList
     */
    public List<MessageCompressionTechnique> getMsgCompList() {
        return msgCompList;
    }

    /**
     * Set msgCompList
     *
     * @param msgCompList
     *            the new value.
     */
    public void setMsgCompList(final List<MessageCompressionTechnique> msgCompList) {
        this.msgCompList = msgCompList;
    }

    /**
     * Get defaultDestinationType
     *
     * @return defaultDestinationType
     */
    public DestinationType getDefaultDestinationType() {
        return defaultDestinationType;
    }

    /**
     * Set defaultDestinationType
     *
     * @param defaultDestinationType
     *            the new value.
     */
    public void setDefaultDestinationType(final DestinationType defaultDestinationType) {
        this.defaultDestinationType = defaultDestinationType;
    }

    /**
     * Get hdrCompList
     *
     * @return hdrCompList
     */
    public List<HeaderCompressionTechnique> getHdrCompList() {
        return hdrCompList;
    }

    /**
     * Set hdrCompList
     *
     * @param hdrCompList
     *            the new value.
     */
    public void setHdrCompList(final List<HeaderCompressionTechnique> hdrCompList) {
        this.hdrCompList = hdrCompList;
    }
}
