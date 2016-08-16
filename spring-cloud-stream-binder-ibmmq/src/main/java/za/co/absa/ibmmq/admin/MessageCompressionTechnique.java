/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.ibmmq.admin;

import com.ibm.msg.client.wmq.WMQConstants;

/**
 * @author TCSASSEMBLER
 * @version 1.0
 */
public enum MessageCompressionTechnique {

    NONE(WMQConstants.WMQ_COMPMSG_NONE),

    RLE(WMQConstants.WMQ_COMPMSG_RLE),

    DEFAULT(WMQConstants.WMQ_COMPMSG_DEFAULT),

    ZLIBHIGH(WMQConstants.WMQ_COMPMSG_ZLIBHIGH),

    ZLIBFAST(WMQConstants.WMQ_COMPMSG_ZLIBFAST);

    private final int code;

    MessageCompressionTechnique(final int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
