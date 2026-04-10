package com.ale.venus.core.im;

import java.util.Collection;

/**
 * 群组管理器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface GroupManager {

    /**
     * 加入群组
     *
     * @param groupId 群组id
     * @param userId  用户id
     */
    void joinGroup(String groupId, String userId);

    /**
     * 获取群组成员
     *
     * @param groupId 群组id
     * @return 群组成员
     */
    Collection<String> getGroupMembers(String groupId);

    /**
     * 离开群组
     *
     * @param groupId 群组id
     * @param userId  用户id
     */
    void leaveGroup(String groupId, String userId);

    /**
     * 删除群组
     *
     * @param groupId 群组id
     */
    void removeGroup(String groupId);

}
