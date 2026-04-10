package com.ale.venus.core.im.netty;

import io.netty.channel.Channel;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单通道管理器
 *
 * @author Ale
 * @version 1.0.0
 */
public class SimpleChannelManager implements ChannelManager {

    /**
     * 用户通道映射
     */
    private static final ConcurrentHashMap<String, Channel> USER_CHANNEL_MAPPING = new ConcurrentHashMap<>();

    /**
     * 通道用户映射
     */
    private static final ConcurrentHashMap<Channel, String> CHANNEL_USER_MAPPING = new ConcurrentHashMap<>();

    @Override
    public void add(String userId, Channel channel) {
        Channel old = USER_CHANNEL_MAPPING.put(userId, channel);
        if (old != null && old != channel) {
            old.close();
            CHANNEL_USER_MAPPING.remove(old);
        }
        CHANNEL_USER_MAPPING.put(channel, userId);
    }

    @Override
    public Channel get(String userId) {
        return USER_CHANNEL_MAPPING.get(userId);
    }

    @Override
    public String getUserId(Channel channel) {
        return CHANNEL_USER_MAPPING.get(channel);
    }

    @Override
    public Collection<String> allUserIds() {
        return USER_CHANNEL_MAPPING.keySet();
    }

    @Override
    public void remove(Channel channel) {
        String userId = CHANNEL_USER_MAPPING.remove(channel);
        if (userId != null) {
            USER_CHANNEL_MAPPING.remove(userId);
        }
    }

}
