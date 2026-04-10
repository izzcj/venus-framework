package com.ale.venus.core.im;

import com.ale.venus.common.security.TokenManager;
import com.ale.venus.core.im.mq.RabbitMessageConsumer;
import com.ale.venus.core.im.mq.RabbitMessageProducer;
import com.ale.venus.core.im.netty.*;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 实时消息自动配置
 *
 * @author Ale
 * @version 1.0.0
 */
@AutoConfiguration
@RequiredArgsConstructor
@ConditionalOnBooleanProperty(prefix = "venus.im", name = "enabled")
@EnableConfigurationProperties(InstantMessageProperties.class)
public class InstantMessageAutoConfig {

    /**
     * 配置属性
     */
    private final InstantMessageProperties properties;

    /**
     * 通道管理器
     *
     * @return {@link ChannelManager}
     */
    @Bean
    @ConditionalOnMissingBean
    public ChannelManager channelManager() {
        return new SimpleChannelManager();
    }

    /**
     * 群组管理器
     *
     * @return {@link GroupManager}
     */
    @Bean
    @ConditionalOnMissingBean
    public GroupManager groupManager() {
        return new RedisGroupManager();
    }

    /**
     * 鉴权处理器
     *
     * @param tokenManager   token管理器
     * @param channelManager 通道管理器
     * @return {@link AuthHandler}
     */
    @Bean
    public AuthHandler authHandler(TokenManager tokenManager, ChannelManager channelManager) {
        return new AuthHandler(tokenManager, channelManager);
    }

    /**
     * 实时消息发送器
     *
     * @param channelManager 通道管理器
     * @param groupManager   群组管理器
     * @return {@link InstantMessageSender}
     */
    @Bean
    @ConditionalOnMissingBean
    public InstantMessageSender immediateMessageSender(ChannelManager channelManager, GroupManager groupManager) {
        return new WebSocketInstantMessageSender(channelManager, groupManager);
    }

    /**
     * WebSocket处理器
     *
     * @param channelManager         通道管理器
     * @param groupManager           群组管理器
     * @param instantMessageSender 即时消息发送器
     * @return {@link WebSocketHandler}
     */
    @Bean
    public WebSocketHandler webSocketHandler(ChannelManager channelManager, GroupManager groupManager, InstantMessageSender instantMessageSender) {
        return new WebSocketHandler(channelManager, groupManager, instantMessageSender);
    }

    /**
     * WebSocket初始化器
     *
     * @param authHandler      鉴权处理器
     * @param webSocketHandler WebSocket处理器
     * @return {@link WebSocketInitializer}
     */
    @Bean
    @ConditionalOnBean(WebSocketHandler.class)
    public WebSocketInitializer webSocketInitializer(AuthHandler authHandler, WebSocketHandler webSocketHandler) {
        return new WebSocketInitializer(authHandler, webSocketHandler);
    }

    /**
     * Netty服务端
     *
     * @param webSocketInitializer WebSocket初始化器
     * @return {@link NettyServer}
     */
    @Bean
    @ConditionalOnBean(WebSocketInitializer.class)
    public NettyServer nettyServer(WebSocketInitializer webSocketInitializer) {
        return new NettyServer(this.properties, webSocketInitializer);
    }

    /**
     * RabbitMq即时消息自动配置
     */
    @RequiredArgsConstructor
    @ConditionalOnProperty(prefix = "venus.im", name = "sender", havingValue = "rabbit")
    @ConditionalOnClass({ RabbitTemplate.class, Channel.class })
    public static class RabbitMqImmediateMessageConfiguration {

        /**
         * 交换机
         */
        public static final String EXCHANGE = "im.exchange";

        /**
         * 队列
         */
        public static final String QUEUE = "im.queue";

        /**
         * 创建交换机
         *
         * @return {@link FanoutExchange}
         */
        @Bean
        public FanoutExchange exchange() {
            return new FanoutExchange(EXCHANGE);
        }

        /**
         * 创建队列
         *
         * @return {@link Queue}
         */
        @Bean
        public Queue queue() {
            return new Queue(QUEUE);
        }

        /**
         * 绑定队列和交换机
         *
         * @return {@link Binding}
         */
        @Bean
        public Binding binding() {
            return BindingBuilder.bind(queue()).to(exchange());
        }

        /**
         * 消息转换器
         *
         * @return {@link MessageConverter}
         */
        @Bean
        public MessageConverter jackson2JsonMessageConverter() {
            return new Jackson2JsonMessageConverter();
        }

        /**
         * 即时消息发送器
         *
         * @param rabbitTemplate RabbitMq模板
         * @return {@link InstantMessageSender}
         */
        @Bean
        public InstantMessageSender immediateMessageSender(RabbitTemplate rabbitTemplate) {
            return new RabbitMessageProducer(rabbitTemplate);
        }

        /**
         * 消息消费者
         *
         * @param channelManager 通道管理器
         * @param groupManager   群组管理器
         * @return {@link RabbitMessageConsumer}
         */
        @Bean
        public RabbitMessageConsumer messageConsumer(ChannelManager channelManager, GroupManager groupManager) {
            return new RabbitMessageConsumer(channelManager, groupManager);
        }
    }

}
