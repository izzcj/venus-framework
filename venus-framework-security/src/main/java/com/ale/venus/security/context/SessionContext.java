package com.ale.venus.security.context;

import com.ale.venus.common.data.DataRepository;
import com.ale.venus.common.security.ExpirationAwareToken;

/**
 * 会话上下文
 *
 * @author Ale
 * @version 1.0.0
 */
public interface SessionContext extends DataRepository {

    /**
     * 获取一个值
     *
     * @param <T>     值类型
     * @param key     键名
     * @param tokenId Token唯一标识
     * @return 值
     */
    <T> T get(String key, String tokenId);

    /**
     * 设置数据
     *
     * @param <T> 期望值类型
     * @param key   键
     * @param value 值
     * @param tokenId Token唯一标识
     * @return 如果对应key的值已经存在，则返回旧的值
     */
    <T> T set(String key, T value, String tokenId);

    /**
     * 刷新会话过期时间
     *
     * @param expirationAwareToken 认证Token
     */
    void refreshExpiration(ExpirationAwareToken expirationAwareToken);

    /**
     * 过期会话
     *
     * @param expirationAwareToken 认证Token
     */
    void expiresSession(ExpirationAwareToken expirationAwareToken);

    /**
     * 过期会话
     *
     * @param tokenId 认证Token唯一标识
     */
    void expiresSession(String tokenId);

}
