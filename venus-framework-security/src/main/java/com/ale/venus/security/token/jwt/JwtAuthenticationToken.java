package com.ale.venus.security.token.jwt;

import com.ale.venus.common.security.AuthenticatedUser;
import com.ale.venus.common.security.ExpirationAwareToken;
import lombok.EqualsAndHashCode;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Date;

/**
 * 基于jwt的认证token
 *
 * @author Ale
 * @version 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
public class JwtAuthenticationToken extends AbstractAuthenticationToken implements ExpirationAwareToken {

    /**
     * Token唯一标识
     */
    private final String tokenId;

    /**
     * Token过期日期
     */
    private Date expiresIn;

    /**
     * 认证用户
     */
    private final AuthenticatedUser authenticatedUser;

    /**
     * Creates a token with the supplied array of authorities.
     */
    public JwtAuthenticationToken(String tokenId, Date expiresIn, AuthenticatedUser authenticatedUser) {
        super(authenticatedUser.getAuthorities());
        this.tokenId = tokenId;
        this.expiresIn = expiresIn;
        this.authenticatedUser = authenticatedUser;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.authenticatedUser;
    }


    @Override
    public String getTokenId() {
        return this.tokenId;
    }

    @Override
    public Date getExpiresIn() {
        return this.expiresIn;
    }

    @Override
    public void setExpiresIn(Date expiresIn) {
        this.expiresIn = expiresIn;
    }
}
