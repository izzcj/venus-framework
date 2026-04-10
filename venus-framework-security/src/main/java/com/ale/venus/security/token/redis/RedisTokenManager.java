package com.ale.venus.security.token.redis;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import com.ale.venus.common.exception.ExceptionCode;
import com.ale.venus.common.security.AuthenticatedUser;
import com.ale.venus.common.security.ExpirationAwareToken;
import com.ale.venus.common.utils.CacheUtils;
import com.ale.venus.common.utils.RedisUtils;
import com.ale.venus.security.context.SessionContext;
import com.ale.venus.security.exception.VenusSecurityException;
import com.ale.venus.security.token.AbstractTokenManager;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Duration;
import java.util.Date;

/**
 * redis Token管理器
 *
 * @author Ale
 * @version 1.0.0
 */
public class RedisTokenManager extends AbstractTokenManager {

    /**
     * Token存储Key前缀
     */
    private static final String TOKEN_KEY_PREFIX = CacheUtils.buildCacheKeyWithPrefix("redisAuthenticationToken");

    /**
     * 认证Token缓存器
     */
    private final LoadingCache<String, RedisAuthenticationToken> authenticationTokenCache;

    /**
     * Token刷新缓存器
     */
    private final LoadingCache<String, Boolean> tokenRefreshCache;

    /**
     * Token过期时长
     */
    private final Duration tokenExpiration;

    /**
     * 会话上下文
     */
    private final SessionContext sessionContext;

    public RedisTokenManager(Duration tokenExpiration, TaskExecutor taskExecutor, SessionContext sessionContext) {
        this.tokenExpiration = tokenExpiration == null || tokenExpiration.isZero() || tokenExpiration.isNegative()
            ? Duration.ofHours(2)
            : tokenExpiration;
        this.sessionContext = sessionContext;

        this.authenticationTokenCache = Caffeine.newBuilder()
            .executor(taskExecutor)
            .expireAfterWrite(Duration.ofMinutes(10))
            .build(RedisUtils::get);

        this.tokenRefreshCache = Caffeine.newBuilder()
            .executor(taskExecutor)
            .expireAfterWrite(Duration.ofMinutes(10))
            .build(key -> RedisUtils.setExpiration(key, this.tokenExpiration));
    }

    @Override
    public Authentication extractAuthentication(String accessToken) {
        RedisAuthenticationToken authenticationToken = this.authenticationTokenCache.get(
            CacheUtils.buildCacheKey(TOKEN_KEY_PREFIX, accessToken)
        );

        if (authenticationToken == null) {
            throw new VenusSecurityException(ExceptionCode.AUTHORIZED_EXPIRE);
        }

        return authenticationToken;
    }

    @Override
    public String generateAccessToken(Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        String accessToken = IdUtil.fastSimpleUUID().toUpperCase();
        RedisAuthenticationToken authenticationToken = new RedisAuthenticationToken(
            accessToken,
            new Date(System.currentTimeMillis() + this.tokenExpiration.toMillis()),
            authenticatedUser
        );

        RedisUtils.set(
            CacheUtils.buildCacheKey(TOKEN_KEY_PREFIX, accessToken),
            authenticationToken,
            this.tokenExpiration
        );

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        return accessToken;
    }

    @Override
    public String generateRefreshToken(Authentication authentication) {
        return null;
    }

    @Override
    public Duration getAccessTokenExpiration() {
        return this.tokenExpiration;
    }

    @Override
    public void expiresToken(ExpirationAwareToken token) {
        this.expiresToken(token.getTokenId());
    }

    @Override
    public void expiresToken(String tokenId) {
        String key = CacheUtils.buildCacheKey(TOKEN_KEY_PREFIX, tokenId);
        RedisUtils.delete(key);
        this.authenticationTokenCache.invalidate(key);
        this.tokenRefreshCache.invalidate(key);

        this.sessionContext.expiresSession(tokenId);
    }

    @Override
    public boolean isTokenExpired(ExpirationAwareToken token) {
        return this.isTokenExpired(token.getTokenId());
    }

    @Override
    public boolean isTokenExpired(String tokenId) {
        return this.authenticationTokenCache.get(CacheUtils.buildCacheKey(TOKEN_KEY_PREFIX, tokenId)) == null;
    }

    @Override
    public void refreshAccessTokenExpiration(ExpirationAwareToken token) {
        Boolean result = this.tokenRefreshCache.get(CacheUtils.buildCacheKey(TOKEN_KEY_PREFIX, token.getTokenId()));
        if (BooleanUtil.isTrue(result)) {
            token.setExpiresIn(
                new Date(
                    System.currentTimeMillis() + this.tokenExpiration.toMillis()
                )
            );

            this.sessionContext.refreshExpiration(token);
            return;
        }

        // 否则登录失效
        throw new VenusSecurityException(ExceptionCode.AUTHORIZED_EXPIRE);
    }

    @Override
    public void refreshAccessTokenExpiration(String tokenId) {
        Boolean result = this.tokenRefreshCache.get(CacheUtils.buildCacheKey(TOKEN_KEY_PREFIX, tokenId));
        if (BooleanUtil.isTrue(result)) {
            Authentication authentication = this.extractAuthentication(tokenId);
            if (authentication instanceof ExpirationAwareToken token) {
                token.setExpiresIn(
                    new Date(
                        System.currentTimeMillis() + this.tokenExpiration.toMillis()
                    )
                );

                this.sessionContext.refreshExpiration(token);
            }
            return;
        }

        // 否则登录失效
        throw new VenusSecurityException(ExceptionCode.AUTHORIZED_EXPIRE);
    }
}
