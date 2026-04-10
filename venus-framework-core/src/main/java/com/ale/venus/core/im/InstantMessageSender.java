package com.ale.venus.core.im;

/**
 * 实时消息发送器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface InstantMessageSender {

    /**
     * 发送消息
     *
     * @param message 消息
     */
    void send(InstantMessage message);

}
