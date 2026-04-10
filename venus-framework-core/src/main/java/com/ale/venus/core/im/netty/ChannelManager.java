package com.ale.venus.core.im.netty;

import io.netty.channel.Channel;
import java.util.Collection;

/**
 * 通道管理器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface ChannelManager {

    /**
     * 添加通道
     *
     * @param userId  用户id
     * @param channel 通道
     */
    void add(String userId, Channel channel);

    /**
     * 获取通道
     *
     * @param userId 用户id
     * @return {@link Channel}
     */
    Channel get(String userId);

    /**
     * 获取用户id
     *
     * @param channel 通道
     * @return 用户id
     */
    String getUserId(Channel channel);

    /**
     * 获取所有UserId
     *
     * @return 通道集合
     */
    Collection<String> allUserIds();

    /**
     * 移除通道
     *
     * @param channel 通道
     */
    void remove(Channel channel);
}
