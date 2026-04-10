package com.ale.venus.security.token.jwt;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.ale.venus.common.exception.ExceptionCode;
import com.ale.venus.common.security.AuthenticatedUser;
import com.ale.venus.common.security.ExpirationAwareToken;
import com.ale.venus.common.utils.CacheUtils;
import com.ale.venus.common.utils.RedisUtils;
import com.ale.venus.security.context.SessionContext;
import com.ale.venus.security.exception.VenusSecurityException;
import com.ale.venus.security.support.AuthenticatedUserConverter;
import com.ale.venus.security.token.AbstractTokenManager;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * JWT Token管理器
 *
 * @author Ale
 * @version 1.0.0
 */
public class JwtTokenManager extends AbstractTokenManager {

    /**
     * 过期Token存储Key前缀
     */
    private static final String EXPIRED_TOKEN_KEY_PREFIX = CacheUtils.buildCacheKeyWithPrefix("expiredToken");

    /**
     * 过期Token存储值
     */
    private static final String EXPIRED_TOKEN_VALUE = "EXPIRED";

    /**
     * JWT主体
     */
    private static final String JWT_SUBJECT = "venus";

    /**
     * Jwt密钥
     */
    private final SecretKey secretKey;

    /**
     * Jwt解析器
     */
    private final JwtParser parser;

    /**
     * Token过期时长
     */
    private final Duration tokenExpiration;

    /**
     * 认证用户转换器
     */
    private final AuthenticatedUserConverter authenticatedUserConverter;

    /**
     * 会话上下文
     */
    private final SessionContext sessionContext;

    public JwtTokenManager(Duration tokenExpiration, AuthenticatedUserConverter authenticatedUserConverter, SessionContext sessionContext) {
        this.tokenExpiration = tokenExpiration == null || tokenExpiration.isZero() || tokenExpiration.isNegative()
            ? Duration.ofHours(2)
            : tokenExpiration;
        this.sessionContext = sessionContext;

        try {
            this.secretKey = Keys.hmacShaKeyFor(new ClassPathResource("jwt.key").getInputStream().readAllBytes());
        } catch (IOException e) {
            throw new VenusSecurityException("加载JWT密钥文件失败：{}", e, e.getMessage());
        }

        this.parser = Jwts.parserBuilder()
            .setSigningKey(this.secretKey)
            .requireSubject(JWT_SUBJECT)
            .build();

        this.authenticatedUserConverter = authenticatedUserConverter;
    }

    @Override
    public Authentication extractAuthentication(String accessToken) {
        Jws<Claims> claimsJws;
        try {
            claimsJws = this.parser.parseClaimsJws(accessToken);
        } catch (ExpiredJwtException e) {
            throw new VenusSecurityException(ExceptionCode.AUTHORIZED_EXPIRE);
        } catch (MalformedJwtException | SignatureException e) {
            throw new VenusSecurityException(ExceptionCode.AUTHORIZED_INVALID);
        }

        Claims claims = claimsJws.getBody();
        var authenticationToken = new JwtAuthenticationToken(
            claims.getId(), claims.getExpiration(), this.authenticatedUserConverter.convertToUser(claims)
        );

        if (this.isTokenExpired(authenticationToken)) {
            throw new VenusSecurityException(ExceptionCode.AUTHORIZED_EXPIRE);
        }

        return authenticationToken;
    }

    @Override
    public String generateAccessToken(Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        String tokenId = IdUtil.fastSimpleUUID();
        var jwtToken = Jwts.builder()
            .signWith(this.secretKey)
            .setId(tokenId)
            .setSubject(JWT_SUBJECT)
            .setExpiration(new Date(System.currentTimeMillis() + this.tokenExpiration.toMillis()))
            .addClaims(this.authenticatedUserConverter.convertToMap(authenticatedUser))
            .compact();

        SecurityContextHolder.getContext().setAuthentication(
            new JwtAuthenticationToken(
                tokenId,
                new Date(System.currentTimeMillis() + this.tokenExpiration.toMillis()),
                authenticatedUser
            )
        );

        return jwtToken;
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
        // token放入过期缓存中
        Duration duration = Duration.between(
            LocalDateTime.now(),
            DateUtil.toLocalDateTime(token.getExpiresIn())
        );

        if (duration.isNegative() || duration.isZero()) {
            return;
        }

        RedisUtils.setString(
            CacheUtils.buildCacheKey(EXPIRED_TOKEN_KEY_PREFIX, token.getTokenId()),
            EXPIRED_TOKEN_VALUE,
            duration
        );

        this.sessionContext.expiresSession(token);
    }

    @Override
    public void expiresToken(String tokenId) {
        // Jwt为无状态的，无法自身实现过期逻辑
    }

    @Override
    public boolean isTokenExpired(ExpirationAwareToken token) {
        return this.isTokenExpired(token.getTokenId());
    }

    @Override
    public boolean isTokenExpired(String tokenId) {
        return RedisUtils.has(CacheUtils.buildCacheKey(EXPIRED_TOKEN_KEY_PREFIX, tokenId));
    }
}
