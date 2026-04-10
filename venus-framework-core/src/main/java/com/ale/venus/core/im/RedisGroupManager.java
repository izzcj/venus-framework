package com.ale.venus.core.im;

import cn.hutool.core.collection.CollectionUtil;
import com.ale.venus.common.utils.CacheUtils;
import com.ale.venus.common.utils.RedisUtils;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;

/**
 * 基于Redis的群组管理器
 *
 * @author Ale
 * @version 1.0.0
 */
public class RedisGroupManager implements GroupManager {

    /**
     * 群组键前缀
     */
    private static final String IMMEDIATE_MESSAGE_GROUP_KEY_PREFIX = CacheUtils.buildCacheKeyWithPrefix("immediateMessageGroup");

    @Override
    public void joinGroup(String groupId, String userId) {
        String cacheKey = this.buildCacheKey(groupId);
        Set<String> groupMembers = RedisUtils.compute(cacheKey, k -> Sets.newHashSet());
        groupMembers.add(userId);
        RedisUtils.set(cacheKey, groupMembers);
    }

    @Override
    public Collection<String> getGroupMembers(String groupId) {
        return RedisUtils.get(this.buildCacheKey(groupId));
    }

    @Override
    public void leaveGroup(String groupId, String userId) {
        String cacheKey = this.buildCacheKey(groupId);
        Collection<String> groupMembers = RedisUtils.get(cacheKey);
        if (CollectionUtil.isEmpty(groupMembers)) {
            return;
        }
        groupMembers.remove(userId);
        RedisUtils.set(cacheKey, groupMembers);
    }

    @Override
    public void removeGroup(String groupId) {
        RedisUtils.delete(this.buildCacheKey(groupId));
    }

    /**
     * 构建缓存键
     *
     * @param groupId 群组ID
     * @return 缓存键
     */
    private String buildCacheKey(String groupId) {
        return CacheUtils.buildCacheKey(IMMEDIATE_MESSAGE_GROUP_KEY_PREFIX, groupId);
    }
}
