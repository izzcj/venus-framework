package com.ale.venus.security.event;

import com.ale.venus.common.security.AuthenticatedUser;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 登录成功事件
 *
 * @author Ale
 * @version 1.0.0
 */
@Getter
public class VenusLoginSuccessEvent extends ApplicationEvent {

    /**
     * 授权用户
     */
    private final AuthenticatedUser authenticatedUser;

    public VenusLoginSuccessEvent(Object source, AuthenticatedUser authenticatedUser) {
        super(source);
        this.authenticatedUser = authenticatedUser;
    }
}
