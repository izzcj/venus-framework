package com.ale.venus.core.im.netty;

import com.ale.venus.core.im.GroupManager;
import com.ale.venus.core.im.InstantMessage;
import com.ale.venus.core.im.InstantMessageHandleSupport;
import com.ale.venus.core.im.InstantMessageSender;

/**
 * 基于WebSocket的即时消息发送器
 *
 * @author Ale
 * @version 1.0.0
 */
public class WebSocketInstantMessageSender extends InstantMessageHandleSupport implements InstantMessageSender {

    public WebSocketInstantMessageSender(ChannelManager channelManager, GroupManager groupManager) {
        super(channelManager, groupManager);
    }

    @Override
    public void send(InstantMessage message) {
        super.handleMessage(message);
    }
}
