package com.ale.venus.common.security;

import org.springframework.security.core.Authentication;

import java.time.Duration;

/**
 * token管理器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface TokenManager {

    /**
     * 从请求中获取访问Token
     *
     * @param authorization Authorization头部
     * @return 访问Token
     */
    String extractAccessToken(String authorization);

    /**
     * 根据访问Token获取认证信息
     *
     * @param accessToken 访问Token
     * @return 认证信息
     */
    Authentication extractAuthentication(String accessToken);

    /**
     * 生成访问Token
     *
     * @param authentication 认证信息
     * @return 访问Token
     */
    String generateAccessToken(Authentication authentication);

    /**
     * 生成刷新Token
     *
     * @param authentication 认证信息
     * @return 刷新Token
     */
    String generateRefreshToken(Authentication authentication);

    /**
     * 获取访问Token过期时长
     *
     * @return 访问Token过期时长
     */
    Duration getAccessTokenExpiration();

    /**
     * 过期一个Token
     *
     * @param token Token
     */
    void expiresToken(ExpirationAwareToken token);

    /**
     * 过期一个Token
     *
     * @param tokenId Token唯一标识
     */
    void expiresToken(String tokenId);

    /**
     * 判断一个Token是否已经过期
     *
     * @param token Token
     * @return bool
     */
    boolean isTokenExpired(ExpirationAwareToken token);

    /**
     * 判断一个Token是否已经过期
     *
     * @param tokenId Token唯一标识
     * @return bool
     */
    boolean isTokenExpired(String tokenId);

    /**
     * 刷新一个Token的过期时间
     *
     * @param token Token
     */
    default void refreshAccessTokenExpiration(ExpirationAwareToken token) {
    }

    /**
     * 刷新一个Token的过期时间
     *
     * @param tokenId Token唯一标识
     */
    default void refreshAccessTokenExpiration(String tokenId) {
    }
}
