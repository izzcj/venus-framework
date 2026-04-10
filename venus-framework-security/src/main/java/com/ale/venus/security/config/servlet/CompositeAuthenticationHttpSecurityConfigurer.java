package com.ale.venus.security.config.servlet;

import com.ale.venus.security.authentication.CompositeAuthenticationConfigurer;
import com.ale.venus.security.authentication.CompositeAuthenticationFilter;
import com.ale.venus.security.authentication.CompositeAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 聚合认证安全配置器
 *
 * @author Ale
 * @version 1.0.0
 */
@RequiredArgsConstructor
public class CompositeAuthenticationHttpSecurityConfigurer implements HttpSecurityConfigurer {

    /**
     * 认证成功处理器
     */
    private final AuthenticationSuccessHandler authenticationSuccessHandler;

    /**
     * 认证失败处理器
     */
    private final AuthenticationFailureHandler authenticationFailureHandler;

    /**
     * 登录处理器持有器
     */
    private final LoginProcessorsHolder loginProcessorsHolder;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authenticationProvider(
            new CompositeAuthenticationProvider(this.loginProcessorsHolder.getLoginProcessors())
        );

        // 优先执行自定义认证逻辑
        Field filterOrdersField = http.getClass().getDeclaredField("filterOrders");
        filterOrdersField.setAccessible(true);
        Object filterOrders = filterOrdersField.get(http);
        Method getOrderMethod = filterOrdersField.getType().getDeclaredMethod("getOrder", Class.class);
        getOrderMethod.setAccessible(true);
        int order = (int) getOrderMethod.invoke(filterOrders, UsernamePasswordAuthenticationFilter.class);
        Method putMethod = filterOrdersField.getType().getDeclaredMethod("put", Class.class, int.class);
        putMethod.setAccessible(true);
        putMethod.invoke(filterOrders, CompositeAuthenticationFilter.class, order + 10);
        http.with(
            new CompositeAuthenticationConfigurer<>(),
            configure -> configure.successHandler(this.authenticationSuccessHandler)
                .failureHandler(this.authenticationFailureHandler)
        );
    }

}
