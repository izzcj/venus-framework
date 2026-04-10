package com.ale.venus.security.config.servlet;

import com.ale.venus.security.authentication.VenusAuthenticationFailureHandler;
import com.ale.venus.security.authentication.VenusAuthenticationSuccessHandler;
import com.ale.venus.security.authentication.VenusLogoutHandler;
import com.ale.venus.security.authentication.VenusLogoutSuccessHandler;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录配置类选择器
 *
 * @author Ale
 * @version 1.0.0
 */
public class LoginConfigurationClassSelector implements ImportSelector {

    @NonNull
    @Override
    public String[] selectImports(@NonNull AnnotationMetadata importingClassMetadata) {
        var annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableVenusAuthentication.class.getName()));
        Assert.notNull(annotationAttributes, "注解EnableVefAuthentication属性不能为空");
        List<String> classNames = new ArrayList<>();
        classNames.add(VenusAuthenticationSuccessHandler.class.getName());
        classNames.add(VenusAuthenticationFailureHandler.class.getName());
        classNames.add(VenusLogoutHandler.class.getName());
        classNames.add(VenusLogoutSuccessHandler.class.getName());
        classNames.add(LoginProcessorsHolder.class.getName());
        classNames.add(LoginHttpSecurityConfigurer.class.getName());
        classNames.add(CompositeAuthenticationHttpSecurityConfigurer.class.getName());
        if (annotationAttributes.getBoolean("limitLoginRate")) {
            classNames.add(LoginRateLimitHttpSecurityConfigurer.class.getName());
        }
        return classNames.toArray(new String[0]);
    }

}
