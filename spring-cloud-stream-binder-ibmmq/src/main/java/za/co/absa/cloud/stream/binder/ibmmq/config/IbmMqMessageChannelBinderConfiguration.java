/*
 * Copyright (C) 2016 TopCoder Inc., All Rights Reserved
 */

package za.co.absa.cloud.stream.binder.ibmmq.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.config.codec.kryo.KryoCodecAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.codec.Codec;

import za.co.absa.cloud.stream.binder.ibmmq.IbmMqMessageChannelBinder;
import za.co.absa.ibmmq.admin.IbmMqAdmin;
import za.co.absa.ibmmq.admin.IbmMqConnectionManager;
import za.co.absa.ibmmq.admin.IbmMqNativeClient;
import za.co.absa.ibmmq.admin.IbmMqProperties;
import za.co.absa.ibmmq.admin.commands.CreateQueueCommand;
import za.co.absa.ibmmq.admin.commands.CreateSubscriptionCommand;
import za.co.absa.ibmmq.admin.commands.CreateTopicCommand;
import za.co.absa.ibmmq.admin.commands.InquireQueueCommand;
import za.co.absa.ibmmq.admin.commands.InquireTopicCommand;

/**
 * The entry point for the binder implementation. It is a spring java configuration class that should be configured in
 * the spring.binders class
 *
 * @author ahmed.seddiq
 * @version 1.0
 */
@Configuration
@Import({PropertyPlaceholderAutoConfiguration.class, KryoCodecAutoConfiguration.class })
@EnableConfigurationProperties({IbmMqProperties.class, IbmMqBinderConfigurationProperties.class,
    IbmMqExtendedBindingProperties.class })
public class IbmMqMessageChannelBinderConfiguration {

    @Autowired
    private Codec codec;

    @Autowired
    private IbmMqExtendedBindingProperties extendedBindingProperties;

    @Autowired
    private IbmMqBinderConfigurationProperties binderProperties;

    @Autowired
    private IbmMqProperties ibmMqProperties;

    @Autowired
    private IbmMqConnectionManager ibmMqConnectionManager;

    @Autowired
    private IbmMqNativeClient ibmMqNativeClient;

    @Autowired
    private InquireQueueCommand inquireQueueCommand;

    @Autowired
    private InquireTopicCommand inquireTopicCommand;

    @Autowired
    private CreateQueueCommand createQueueCommand;

    @Autowired
    private CreateTopicCommand createTopicCommand;

    @Autowired
    private CreateSubscriptionCommand createSubscriptionCommand;

    @Autowired
    private IbmMqAdmin ibmMqAdmin;

    @Bean
    public IbmMqMessageChannelBinder ibmMqMessageChannelBinder() {
        final IbmMqMessageChannelBinder bean = new IbmMqMessageChannelBinder();
        bean.setCodec(codec);
        bean.setExtendedBindingProperties(extendedBindingProperties);
        bean.setBinderProperties(binderProperties);
        bean.setIbmMqConnectionManager(ibmMqConnectionManager);
        bean.setIbmMqAdmin(ibmMqAdmin);
        return bean;
    }

    @Bean
    public IbmMqConnectionManager ibmMqConnectionManager() {
        final IbmMqConnectionManager bean = new IbmMqConnectionManager();
        bean.setIbmMqBinderProperties(binderProperties);
        bean.setIbmMqProperties(ibmMqProperties);
        return bean;
    }

    @Bean
    public IbmMqAdmin ibmMqAdmin() {
        final IbmMqAdmin bean = new IbmMqAdmin();
        bean.setCreateQueueCommand(createQueueCommand);
        bean.setInquireQueueCommand(inquireQueueCommand);
        bean.setInquireTopicCommand(inquireTopicCommand);
        bean.setCreateTopicCommand(createTopicCommand);
        bean.setCreateSubscriptionCommand(createSubscriptionCommand);
        return bean;
    }

    @Bean
    public InquireQueueCommand inquireQueueCommand() {
        final InquireQueueCommand bean = new InquireQueueCommand();
        bean.setIbmMqNativeClient(ibmMqNativeClient);
        return bean;
    }

    @Bean
    public InquireTopicCommand inquireTopicCommand() {
        final InquireTopicCommand bean = new InquireTopicCommand();
        bean.setIbmMqNativeClient(ibmMqNativeClient);
        return bean;
    }

    @Bean
    public CreateQueueCommand createQueueCommand() {
        final CreateQueueCommand bean = new CreateQueueCommand();
        bean.setIbmMqProperties(ibmMqProperties);
        bean.setInquireQueueCommand(inquireQueueCommand);
        return bean;
    }

    @Bean
    public CreateTopicCommand createTopicCommand() {
        final CreateTopicCommand bean = new CreateTopicCommand();
        bean.setInquireTopicCommand(inquireTopicCommand);
        return bean;
    }

    @Bean
    public CreateSubscriptionCommand createSubscriptionCommand() {
        return new CreateSubscriptionCommand();
    }

    @Bean
    public IbmMqNativeClient ibmMqNativeClient() {
        final IbmMqNativeClient bean = new IbmMqNativeClient();
        bean.setIbmMqConnectionManager(ibmMqConnectionManager);
        return bean;
    }
}
