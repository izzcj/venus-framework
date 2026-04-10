package com.ale.venus.security.config.servlet;

import com.ale.venus.security.authentication.LoginProcessor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * 登录处理器持有器
 *
 * @author Ale
 * @version 1.0.0
 */
@Getter
@Component
@RequiredArgsConstructor
public class LoginProcessorsHolder {

    /**
     * 登录处理器
     */
    private final ObjectProvider<LoginProcessor> loginProcessors;

}
