/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.ibmmq.admin;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * The basic connectivity and security properties for IBM MQ
 *
 * @author ahmed.seddiq
 * @version 1.0
 */
@ConfigurationProperties(prefix = "spring.ibmmq")
public class IbmMqProperties {
    private String host = "127.0.0.1";
    private Integer port = 1414;
    private String qm = "QM1";
    private String username;
    private String password;
    private String channel = "SYSTEM.DEF.SVRCONN";
    private Integer maxQueueDepth = 100000;
    private String sslCiph;
    private String sslKeyr;
    private String sslPass;

    /**
     * Get channel
     *
     * @return channel
     */
    public String getChannel() {
        return channel;
    }

    /**
     * Set channel
     *
     * @param channel
     *            the new value.
     */
    public void setChannel(final String channel) {
        this.channel = channel;
    }

    /**
     * Get password
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set password
     *
     * @param password
     *            the new value.
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Get port
     *
     * @return port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * Set port
     *
     * @param port
     *            the new value.
     */
    public void setPort(final Integer port) {
        this.port = port;
    }

    /**
     * Get sslPass
     *
     * @return sslPass
     */
    public String getSslPass() {
        return sslPass;
    }

    /**
     * Set sslPass
     *
     * @param sslPass
     *            the new value.
     */
    public void setSslPass(final String sslPass) {
        this.sslPass = sslPass;
    }

    /**
     * Get sslKeyr
     *
     * @return sslKeyr
     */
    public String getSslKeyr() {
        return sslKeyr;
    }

    /**
     * Set sslKeyr
     *
     * @param sslKeyr
     *            the new value.
     */
    public void setSslKeyr(final String sslKeyr) {
        this.sslKeyr = sslKeyr;
    }

    /**
     * Get username
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set username
     *
     * @param username
     *            the new value.
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Get host
     *
     * @return host
     */
    public String getHost() {
        return host;
    }

    /**
     * Set host
     *
     * @param host
     *            the new value.
     */
    public void setHost(final String host) {
        this.host = host;
    }

    /**
     * Get sslCiph
     *
     * @return sslCiph
     */
    public String getSslCiph() {
        return sslCiph;
    }

    /**
     * Set sslCiph
     *
     * @param sslCiph
     *            the new value.
     */
    public void setSslCiph(final String sslCiph) {
        this.sslCiph = sslCiph;
    }

    /**
     * Get qm
     *
     * @return qm
     */
    public String getQm() {
        return qm;
    }

    /**
     * Set qm
     *
     * @param qm
     *            the new value.
     */
    public void setQm(final String qm) {
        this.qm = qm;
    }

    /**
     * Get maxQueueDepth
     *
     * @return maxQueueDepth
     */
    public Integer getMaxQueueDepth() {
        return maxQueueDepth;
    }

    /**
     * Set maxQueueDepth
     *
     * @param maxQueueDepth
     *            the new value.
     */
    public void setMaxQueueDepth(final Integer maxQueueDepth) {
        this.maxQueueDepth = maxQueueDepth;
    }
}
