package com.ale.venus.security.context;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * token上下文
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
public class TokenContext {

    /**
     * 访问Token
     */
    private String accessToken;

    /**
     * 刷新token
     */
    private String refreshToken;

    /**
     * 过期时间
     */
    private Long expiresIn;
}
