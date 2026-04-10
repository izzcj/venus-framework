package com.ale.venus.common.security;

import java.util.Date;

/**
 * 可过期的token
 *
 * @author Ale
 * @version 1.0.0
 */
public interface ExpirationAwareToken {
    /**
     * 获取Token唯一标识
     *
     * @return Token唯一标识
     */
    String getTokenId();

    /**
     * 获取Token过期日期
     *
     * @return Token过期日期
     */
    Date getExpiresIn();

    /**
     * 设置Token过期日期
     *
     * @param expiresIn 过期日期
     */
    void setExpiresIn(Date expiresIn);
}
