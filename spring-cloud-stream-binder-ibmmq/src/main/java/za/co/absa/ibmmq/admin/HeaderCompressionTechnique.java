/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.ibmmq.admin;

import com.ibm.msg.client.wmq.WMQConstants;

/**
 * An enumeration for the supported Header compression techniques
 *
 * @author ahmed.seddiq
 * @version 1.0
 */
public enum HeaderCompressionTechnique {

    SYSTEM(WMQConstants.WMQ_COMPHDR_SYSTEM),

    NONE(WMQConstants.WMQ_COMPHDR_NONE),

    DEFAULT(WMQConstants.WMQ_COMPHDR_DEFAULT);

    private final int code;

    HeaderCompressionTechnique(final int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
