/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.ibmmq.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import za.co.absa.ibmmq.IbmMqException;

import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.pcf.PCFMessageAgent;

/**
 * A wrapper for MQQueueManager and PCFMessageAgent.
 *
 * @author ahmed.seddiq
 * @version 1.0
 */
public class IbmMqNativeClient {
    private IbmMqConnectionManager ibmMqConnectionManager;

    private volatile MQQueueManager queueManager;

    private volatile PCFMessageAgent pcfMessageAgent;

    public MQQueueManager getQueueManager() throws IbmMqException {
        if (queueManager == null) {
            connect();
        }
        return queueManager;
    }

    public PCFMessageAgent getPcfMessageAgent() throws IbmMqException {
        if (pcfMessageAgent == null) {
            connect();
        }
        return pcfMessageAgent;
    }

    public void connect() throws IbmMqException {
        try {
            if (pcfMessageAgent != null) {
                pcfMessageAgent.disconnect();
            }
            queueManager = ibmMqConnectionManager.newQueueManager();
            pcfMessageAgent = new PCFMessageAgent(queueManager);
        } catch (MQException e) {
            throw new IbmMqException("Error connecting to MQ", e);
        }
    }

    public void disconnect() throws IbmMqException {
        try {
            if (pcfMessageAgent != null) {
                pcfMessageAgent.disconnect();
            }
            if (queueManager != null) {
                queueManager.disconnect();
            }
        } catch (MQException e) {
            throw new IbmMqException("Error disconnecting MQ Queue Manager ", e);
        }
    }

    /**
     * Set ibmMqConnectionManager
     *
     * @param ibmMqConnectionManager the new value.
     */
    public void setIbmMqConnectionManager(IbmMqConnectionManager ibmMqConnectionManager) {
        this.ibmMqConnectionManager = ibmMqConnectionManager;
    }

    /**
     * Get ibmMqConnectionManager
     *
     * @return ibmMqConnectionManager
     */
    public IbmMqConnectionManager getIbmMqConnectionManager() {
        return ibmMqConnectionManager;
    }
}
