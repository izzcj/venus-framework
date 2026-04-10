package com.ale.venus.core.im;

import cn.hutool.core.collection.CollectionUtil;
import com.ale.venus.common.utils.JsonUtils;
import com.ale.venus.core.im.netty.ChannelManager;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Collection;
import java.util.Collections;

/**
 * 实时消息处理支持
 *
 * @author Ale
 * @version 1.0.0
 */
public abstract class InstantMessageHandleSupport {

    /**
     * 通道管理器
     */
    private final ChannelManager channelManager;

    /**
     * 群组管理器
     */
    private final GroupManager groupManager;

    public InstantMessageHandleSupport(ChannelManager channelManager, GroupManager groupManager) {
        this.channelManager = channelManager;
        this.groupManager = groupManager;
    }

    /**
     * 处理消息
     *
     * @param message 消息
     */
    protected void handleMessage(InstantMessage message) {
        String messageJson = JsonUtils.toJson(message);
        Collection<String> receiverIds = this.getReceiverIds(message);
        if (CollectionUtil.isEmpty(receiverIds)) {
            return;
        }
        for (String receiverId : receiverIds) {
            if (receiverId.equals(message.getFrom())) {
                continue;
            }
            this.sendMessage(receiverId, messageJson);
        }
    }

    /**
     * 获取接收者ID
     *
     * @param message 消息
     * @return 接收者id集合
     */
    private Collection<String> getReceiverIds(InstantMessage message) {
        InstantMessageType instantMessageType = message.getType();
        if (InstantMessageType.GROUP_CHAT.match(instantMessageType)) {
            return this.groupManager.getGroupMembers(message.getTo());
        }
        if (InstantMessageType.PUSH.match(instantMessageType)) {
            return this.channelManager.allUserIds();
        }
        return Collections.singletonList(message.getTo());
    }

    /**
     * 发送消息
     *
     * @param userId  用户ID
     * @param message 消息
     */
    private void sendMessage(String userId, String message) {
        Channel channel = this.channelManager.get(userId);
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(new TextWebSocketFrame(message));
        }
    }

}
