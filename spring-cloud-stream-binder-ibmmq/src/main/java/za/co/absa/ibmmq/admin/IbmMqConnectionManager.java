/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.ibmmq.admin;

import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import za.co.absa.cloud.stream.binder.ibmmq.config.IbmMqBinderConfigurationProperties;
import za.co.absa.ibmmq.IbmMqException;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;

/**
 * Manages the connections to IBM MQ, it will handle both JMS connection and Native (Queue Manager) connections.
 *
 * @author ahmed.seddiq
 * @version 1.0
 */
public class IbmMqConnectionManager {
    private IbmMqProperties ibmMqProperties;

    private IbmMqBinderConfigurationProperties ibmMqBinderProperties;

    private volatile MQConnectionFactory jmsConnectionFactory;

    /**
     * Initializes connections based on the provided configurations.
     *
     * @throws IbmMqException
     *             if any error occurs during operation.
     */
    @PostConstruct
    public final void init() throws IbmMqException {
        if (ibmMqProperties.getSslKeyr() != null) {
            System.setProperty("javax.net.ssl.keyStore", ibmMqProperties.getSslKeyr());
            System.setProperty("javax.net.ssl.trustStore", ibmMqProperties.getSslKeyr());
        }
        if (ibmMqProperties.getSslPass() != null) {
            System.setProperty("javax.net.ssl.keyStorePassword", ibmMqProperties.getSslPass());
        }
        initJmsConnectionFactory();
        initMQEnvironment();
    }

    /**
     * Initializes the MQEnvironment with values from injected configurations.
     */
    private void initMQEnvironment() {
        MQEnvironment.hostname = ibmMqProperties.getHost();
        MQEnvironment.port = ibmMqProperties.getPort();
        MQEnvironment.channel = ibmMqProperties.getChannel();

        if (ibmMqProperties.getUsername() != null) {
            MQEnvironment.userID = ibmMqProperties.getUsername();
        }
        if (ibmMqProperties.getPassword() != null) {
            MQEnvironment.password = ibmMqProperties.getPassword();
        }
        if (ibmMqProperties.getSslCiph() != null) {
            MQEnvironment.properties.put(MQConstants.SSL_CIPHER_SUITE_PROPERTY, ibmMqProperties.getSslCiph());
        }
    }

    /**
     * Getter for the configured JMS ConnectionFactory
     *
     * @return the configured JMS connection factory.
     */
    public ConnectionFactory getJmsConnectionFactory() {
        return jmsConnectionFactory;
    }

    /**
     * Returns a new MQQueueManager.
     *
     * @throws IbmMqException
     *             if any error occurs during operation.
     */
    public MQQueueManager newQueueManager() throws IbmMqException {
        try {
            return new MQQueueManager(ibmMqProperties.getQm());
        } catch (MQException e) {
            throw new IbmMqException("Error creating QueueManager", e);
        }
    }

    /**
     * Initializes the JMS connection factory with injected configuration values.
     *
     * @throws IbmMqException
     *             if any error occurs during operation.
     */
    private void initJmsConnectionFactory() throws IbmMqException {
        try {
            jmsConnectionFactory = new MQConnectionFactory();
            jmsConnectionFactory.setHostName(ibmMqProperties.getHost());
            jmsConnectionFactory.setPort(ibmMqProperties.getPort());
            jmsConnectionFactory.setQueueManager(ibmMqProperties.getQm());
            jmsConnectionFactory.setChannel(ibmMqProperties.getChannel());
            jmsConnectionFactory.setTransportType(WMQConstants.WMQ_CLIENT_NONJMS_MQ);

            if (ibmMqProperties.getUsername() != null) {
                jmsConnectionFactory.setStringProperty(WMQConstants.USERID, ibmMqProperties.getUsername());
            }
            if (ibmMqProperties.getPassword() != null) {
                jmsConnectionFactory.setStringProperty(WMQConstants.PASSWORD, ibmMqProperties.getPassword());
            }
            if (ibmMqProperties.getSslCiph() != null) {
                jmsConnectionFactory.setSSLCipherSuite(ibmMqProperties.getSslCiph());
            }

            jmsConnectionFactory.setMsgCompList(ibmMqBinderProperties.getMsgCompList().stream()
                    .map(MessageCompressionTechnique::getCode).collect(Collectors.toList()));
            jmsConnectionFactory.setHdrCompList(ibmMqBinderProperties.getHdrCompList().stream()
                    .map(HeaderCompressionTechnique::getCode).collect(Collectors.toList()));

        } catch (JMSException e) {
            throw new IbmMqException("Can't create JMS connection factory", e);
        }
    }


    /**
     * Get ibmMqProperties
     *
     * @return ibmMqProperties
     */
    public IbmMqProperties getIbmMqProperties() {
        return ibmMqProperties;
    }

    /**
     * Set ibmMqProperties
     *
     * @param ibmMqProperties the new value.
     */
    public void setIbmMqProperties(final IbmMqProperties ibmMqProperties) {
        this.ibmMqProperties = ibmMqProperties;
    }

    /**
     * Get ibmMqBinderProperties
     *
     * @return ibmMqBinderProperties
     */
    public IbmMqBinderConfigurationProperties getIbmMqBinderProperties() {
        return ibmMqBinderProperties;
    }

    /**
     * Set ibmMqBinderProperties
     *
     * @param ibmMqBinderProperties the new value.
     */
    public void setIbmMqBinderProperties(final IbmMqBinderConfigurationProperties ibmMqBinderProperties) {
        this.ibmMqBinderProperties = ibmMqBinderProperties;
    }
}
