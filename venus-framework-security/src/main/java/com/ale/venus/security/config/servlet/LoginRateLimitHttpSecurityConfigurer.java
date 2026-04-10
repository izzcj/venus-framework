package com.ale.venus.security.config.servlet;

import com.ale.venus.security.filter.LoginRateLimitFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 登录频率限制配置
 *
 * @author Ale
 * @version 1.0.0
 */
public class LoginRateLimitHttpSecurityConfigurer implements HttpSecurityConfigurer {

    @Override
    public void configure(HttpSecurity http) {
        http.addFilterBefore(new LoginRateLimitFilter(), UsernamePasswordAuthenticationFilter.class);
    }

}
