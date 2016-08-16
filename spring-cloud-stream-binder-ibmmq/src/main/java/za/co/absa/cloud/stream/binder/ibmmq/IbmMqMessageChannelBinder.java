/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.cloud.stream.binder.ibmmq;

import java.util.Optional;

import org.springframework.cloud.stream.binder.AbstractMessageChannelBinder;
import org.springframework.cloud.stream.binder.BinderHeaders;
import org.springframework.cloud.stream.binder.ConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.binder.ExtendedPropertiesBinder;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.jms.ChannelPublishingJmsMessageListener;
import org.springframework.integration.jms.JmsMessageDrivenEndpoint;
import org.springframework.integration.jms.JmsOutboundGateway;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

import za.co.absa.cloud.stream.binder.ibmmq.config.IbmMqBaseChannelProperties;
import za.co.absa.cloud.stream.binder.ibmmq.config.IbmMqBinderConfigurationProperties;
import za.co.absa.cloud.stream.binder.ibmmq.config.IbmMqConsumerProperties;
import za.co.absa.cloud.stream.binder.ibmmq.config.IbmMqExtendedBindingProperties;
import za.co.absa.cloud.stream.binder.ibmmq.config.IbmMqProducerProperties;
import za.co.absa.ibmmq.IbmMqException;
import za.co.absa.ibmmq.admin.IbmMqAdmin;
import za.co.absa.ibmmq.admin.IbmMqConnectionManager;

import com.ibm.mq.MQDestination;

/**
 * A MessageChannel Binder implementation for the IBM MQ messaging system. It extends the helper abstract class
 * AbstractMessageChannelBinder and provides the required methods.
 *
 * @author ahmed.seddiq
 * @version 1.0
 */
public class IbmMqMessageChannelBinder extends
        AbstractMessageChannelBinder<ExtendedConsumerProperties<IbmMqConsumerProperties>, ExtendedProducerProperties<IbmMqProducerProperties>, MQDestination>
        implements ExtendedPropertiesBinder<MessageChannel, IbmMqConsumerProperties, IbmMqProducerProperties> {

    /**
     * The binding properties specific to IBM MQ.
     */
    private IbmMqExtendedBindingProperties extendedBindingProperties;

    /**
     * The binder properties specific to IBM MQ.
     */
    private IbmMqBinderConfigurationProperties binderProperties;

    /**
     * An instance of IbmMqConnectionManager that will manage both JMS and Native connections to IBM MQ.
     */
    private IbmMqConnectionManager ibmMqConnectionManager;

    /**
     * A facade for executing IBM MQ administrative tasks
     */
    private IbmMqAdmin ibmMqAdmin;

    /**
     * Default constructor
     */
    public IbmMqMessageChannelBinder() {
        super(true, new String[0]);
    }

    /**
     * Creates target destinations for outbound channels.
     *
     * For Queue destinations, it will create a queue with the provided name
     *
     * For Topic destinations, it will check properties for the configured partitions/groups and will create required
     * durable queues. If no partitions/groups configured, the topic will be created with the given name.
     *
     * @param name
     *            the name of the producer destination
     * @param properties
     *            producer properties
     */
    @Override
    protected void createProducerDestinationIfNecessary(final String name,
        final ExtendedProducerProperties<IbmMqProducerProperties> properties) {
        try {
            final DestinationType destinationType = getDestinationType(properties.getExtension());
            if (DestinationType.TOPIC.equals(destinationType)) {
                if (properties.isPartitioned()) {
                    for (int i = 0; i < properties.getPartitionCount(); i++) {
                        final String topicName = name + '.' + i;
                        if (properties.getRequiredGroups().length > 0) {
                            for (final String group : properties.getRequiredGroups()) {
                                ibmMqAdmin.getOrCreateDurableQueue(topicName, groupedName(topicName, group));
                            }
                        } else {
                            ibmMqAdmin.getOrCreateTopic(topicName);
                        }
                    }
                } else {
                    ibmMqAdmin.getOrCreateTopic(name);
                }
            } else {
                ibmMqAdmin.getOrCreateQueue(name);
            }
        } catch (IbmMqException e) {
            this.logger.error(String.format("Error creating destination (%s)", name), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a {@link JmsOutboundGateway} that will send data to the given destination. it will be stopped
     * automatically by the binder.
     *
     * @param destination
     *            the name of the target destination
     * @param producerProperties
     *            the producer properties
     * @return the {@link JmsOutboundGateway} instance for sending data to the IBM MQ
     * @throws Exception
     */
    @Override
    protected MessageHandler createProducerMessageHandler(final String destination,
        final ExtendedProducerProperties<IbmMqProducerProperties> producerProperties) throws Exception {
        final DestinationType destinationType = getDestinationType(producerProperties.getExtension());

        final boolean useTopic = DestinationType.TOPIC.equals(destinationType);
        final JmsOutboundGateway endpoint = new JmsOutboundGateway();
        endpoint.setReplyPubSubDomain(useTopic);
        endpoint.setRequestPubSubDomain(useTopic);
        endpoint.setConnectionFactory(ibmMqConnectionManager.getJmsConnectionFactory());
        endpoint.setBeanFactory(getBeanFactory());

        if (producerProperties.isPartitioned()) {
            final String destinationExpression = String.format("'%s'.concat('.').concat(headers['%s'])",
                    destination.toUpperCase(), BinderHeaders.PARTITION_HEADER);
            endpoint.setRequestDestinationExpression(EXPRESSION_PARSER.parseExpression(destinationExpression));
        } else {
            endpoint.setRequestDestinationName(destination.toUpperCase());
        }
        endpoint.setCorrelationKey("JMSCorrelationID");
        endpoint.afterPropertiesSet();
        return endpoint;
    }

    /**
     * Creates a {@link MQDestination} instance based on the given parameters and configuration.
     *
     * @param name
     *            the name of the destination
     * @param group
     *            the consumer group
     * @param properties
     *            consumer properties
     * @return reference to the consumer destination
     */
    @Override
    protected MQDestination createConsumerDestinationIfNecessary(final String name, final String group,
        final ExtendedConsumerProperties<IbmMqConsumerProperties> properties) {

        final boolean anonymous = !StringUtils.hasText(group);
        try {
            final DestinationType destinationType = getDestinationType(properties.getExtension());
            if (DestinationType.TOPIC.equals(destinationType)) {
                if (!anonymous) {
                    final String topicName =
                            properties.isPartitioned() ? name + '.' + properties.getInstanceIndex() : name;
                    return ibmMqAdmin.getOrCreateDurableQueue(topicName, groupedName(topicName, group));
                } else {
                    return ibmMqAdmin.getOrCreateTopic(name);
                }
            } else {
                if (!anonymous) {
                    this.logger.warn("Defining group with a QUEUE destination type has no effect");
                }
                return ibmMqAdmin.getOrCreateQueue(name);
            }
        } catch (final IbmMqException e) {
            this.logger.error(String.format("Error creating destination with name (%s)", name), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates {@link MessageProducer} that receives data from the consumer destination. will be started and stopped by
     * the binder.
     *
     * @param name
     *            the name of the target destination
     * @param group
     *            the consumer group
     * @param destination
     *            reference to the consumer destination
     * @param properties
     *            the consumer properties
     * @return the consumer endpoint.
     */
    @Override
    protected MessageProducer createConsumerEndpoint(final String name, final String group,
        final MQDestination destination, final ExtendedConsumerProperties<IbmMqConsumerProperties> properties) {

        // Create a connection factory
        final IbmMqConsumerProperties consumerProperties = properties.getExtension();
        final DestinationType destinationType = getDestinationType(consumerProperties);
        final String destinationName = getDestinationName(name, group, destinationType, properties);

        final boolean useTopic = DestinationType.TOPIC.equals(destinationType) && group == null;
        final DefaultMessageListenerContainer listenerContainer = new DefaultMessageListenerContainer();
        listenerContainer.setConnectionFactory(ibmMqConnectionManager.getJmsConnectionFactory());
        listenerContainer.setDestinationName(destinationName.toUpperCase());
        listenerContainer.setReplyPubSubDomain(useTopic);
        listenerContainer.setPubSubDomain(useTopic);
        listenerContainer.setSessionTransacted(consumerProperties.isTransacted());
        if (consumerProperties.getTransactionManagerBean() != null) {
            listenerContainer.setTransactionManager(getBeanFactory()
                    .getBean(consumerProperties.getTransactionManagerBean(), PlatformTransactionManager.class));
        }
        listenerContainer.afterPropertiesSet();

        final ChannelPublishingJmsMessageListener messageListener = new ChannelPublishingJmsMessageListener();
        messageListener.setBeanFactory(getBeanFactory());
        messageListener.setExpectReply(false);
        messageListener.afterPropertiesSet();

        return new JmsMessageDrivenChannelAdapter(listenerContainer, messageListener);
    }

    /**
     * Helper method to resolve destination name
     *
     * @param name
     *            the base name
     * @param group
     *            the group name
     * @param destinationType
     *            either TOPIC or QUEUE
     * @param properties
     *            the ConsumerProperties of this binding.
     * @return the resolved destination name.
     */
    private String getDestinationName(final String name, final String group, final DestinationType destinationType,
        final ConsumerProperties properties) {
        if (DestinationType.TOPIC.equals(destinationType)) {
            if (group != null) {
                final String topicName = properties.isPartitioned() ? name + '.' + properties.getInstanceIndex() : name;
                return groupedName(topicName, group);
            }
        }
        return name;
    }

    /**
     * Helper method to resolve destination type
     *
     * @param channelProperties
     *            the current channel properties
     * @return the resoved destination type.
     */
    private DestinationType getDestinationType(final IbmMqBaseChannelProperties channelProperties) {
        return Optional.ofNullable(channelProperties.getDestinationType())
                .orElse(binderProperties.getDefaultDestinationType());
    }

    /**
     * Gets the consumer properties specific to IBM MQ.
     *
     * @param channelName
     *            the channel name
     * @return an IbmMqConsumerProperties instance.
     */
    @Override
    public IbmMqConsumerProperties getExtendedConsumerProperties(final String channelName) {
        return extendedBindingProperties.getExtendedConsumerProperties(channelName);
    }

    /**
     * Gets the producer properties specific to IBM MQ.
     *
     * @param channelName
     *            the channel name.
     * @return an IbmMqProducerProperties instance.
     */
    @Override
    public IbmMqProducerProperties getExtendedProducerProperties(final String channelName) {
        return extendedBindingProperties.getExtendedProducerProperties(channelName);
    }

    /**
     * Setter for extendedBindingProperties
     *
     * @param extendedBindingProperties
     *            the extendedBindingProperties value.
     */
    public void setExtendedBindingProperties(final IbmMqExtendedBindingProperties extendedBindingProperties) {
        this.extendedBindingProperties = extendedBindingProperties;
    }

    /**
     * Setter for the binderProperties.
     *
     * @param binderProperties
     *            the binderProperties value.
     */
    public void setBinderProperties(final IbmMqBinderConfigurationProperties binderProperties) {
        this.binderProperties = binderProperties;
    }

    /**
     * Setter for the ibmMqConnectionManager
     *
     * @param ibmMqConnectionManager
     *            the ibmMqConnectionManager value.
     */
    public void setIbmMqConnectionManager(final IbmMqConnectionManager ibmMqConnectionManager) {
        this.ibmMqConnectionManager = ibmMqConnectionManager;
    }

    /**
     * Setter for the ibmMqAdmin.
     *
     * @param ibmMqAdmin
     *            the ibmMqAdmin value.
     */
    public void setIbmMqAdmin(final IbmMqAdmin ibmMqAdmin) {
        this.ibmMqAdmin = ibmMqAdmin;
    }

    /**
     * This class extends the JmsMessageDrivenEndpoint to support the MessageProducer interface.
     */
    private static class JmsMessageDrivenChannelAdapter extends JmsMessageDrivenEndpoint implements MessageProducer {

        private final ChannelPublishingJmsMessageListener listener;

        /**
         * Construct an instance with an externally configured container.
         *
         * @param listenerContainer
         *            the container.
         * @param listener
         *            the listener.
         */
        JmsMessageDrivenChannelAdapter(final AbstractMessageListenerContainer listenerContainer,
                final ChannelPublishingJmsMessageListener listener) {
            super(listenerContainer, listener);
            this.listener = listener;

        }

        /**
         * Sets the given MessageChannel to the request channel of the registered listener.
         *
         * @param outputChannel
         *            the output message channel.
         */
        @Override
        public void setOutputChannel(final MessageChannel outputChannel) {
            this.listener.setRequestChannel(outputChannel);
        }
    }
}
