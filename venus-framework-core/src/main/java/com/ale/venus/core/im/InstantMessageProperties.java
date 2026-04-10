package com.ale.venus.core.im;

import com.ale.venus.common.support.EnableAwareProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 实时消息配置属性
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "venus.im")
public class InstantMessageProperties extends EnableAwareProperties {

    /**
     * 端口
     */
    private int port = 8081;

    /**
     * 消息发送器
     */
    private SenderType sender = SenderType.WEBSOCKET;

    /**
     * 消息发送器类型枚举
     */
    @Getter
    public enum SenderType {
        /**
         * 默认
         */
        WEBSOCKET,

        /**
         * RabbitMQ
         */
        RABBIT,

        /**
         * Kafka
         */
        KAFKA
    }

}
