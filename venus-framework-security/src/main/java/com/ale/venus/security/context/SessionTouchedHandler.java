package com.ale.venus.security.context;

import org.springframework.security.core.Authentication;

/**
 * Session碰撞处理器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface SessionTouchedHandler {

    /**
     * 会话刷新后的处理
     *
     * @param authentication 认证对象
     */
    void onTouched(Authentication authentication);
}
