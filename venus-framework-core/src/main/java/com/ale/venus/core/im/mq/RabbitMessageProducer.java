package com.ale.venus.core.im.mq;

import com.ale.venus.common.constants.StringConstants;
import com.ale.venus.core.im.InstantMessage;
import com.ale.venus.core.im.InstantMessageAutoConfig;
import com.ale.venus.core.im.InstantMessageSender;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * 基于RabbitMq的消息生产者
 *
 * @author Ale
 * @version 1.0.0
 */
public class RabbitMessageProducer implements InstantMessageSender {

    /**
     * RabbitMqTemplate
     */
    private final RabbitTemplate rabbitTemplate;

    /**
     * 构造函数
     *
     * @param template 模板
     */
    public RabbitMessageProducer(RabbitTemplate template) {
        this.rabbitTemplate = template;
    }

    @Override
    public void send(InstantMessage message) {
        this.rabbitTemplate.convertAndSend(InstantMessageAutoConfig.RabbitMqImmediateMessageConfiguration.EXCHANGE, StringConstants.EMPTY, message);
    }

}
