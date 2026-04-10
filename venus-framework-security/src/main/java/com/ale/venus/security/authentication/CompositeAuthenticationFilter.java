package com.ale.venus.security.authentication;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.ale.venus.security.contanst.SecurityConstants;
import com.ale.venus.security.enums.LoginType;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 聚合认证过滤器
 *
 * @author Ale
 * @version 1.0.0
 */
public class CompositeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    protected CompositeAuthenticationFilter() {
        super(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, SecurityConstants.LOGIN_URI));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        String content = IoUtil.read(IoUtil.getReader(request.getInputStream(), StandardCharsets.UTF_8), false);
        if (StrUtil.isBlank(content)) {
            throw new AuthenticationServiceException("请求体为空");
        }
        if (!JSON.isValidObject(content)) {
            throw new AuthenticationServiceException("请求体必须为JSON");
        }

        JSONObject jsonObject = JSON.parseObject(content);
        String loginType = jsonObject.getString("loginType");
        if (StrUtil.isBlank(loginType)) {
            throw new AuthenticationServiceException("登录类型不能为空");
        }

        jsonObject.remove("loginType");
        if (jsonObject.isEmpty()) {
            throw new AuthenticationServiceException("登录参数不能为空");
        }
        LoginType loginTypeEnum;
        try {
            loginTypeEnum = LoginType.valueOf(LoginType.class, loginType);
        } catch (Exception e) {
            throw new AuthenticationServiceException("登录类型不存在");
        }
        CompositeAuthenticationToken authRequest = CompositeAuthenticationToken.unauthenticated(
            loginTypeEnum,
            jsonObject
        );
        // Allow subclasses to set the "details" property
        this.setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * Provided so that subclasses may configure what is put into the authentication
     * request's details property.
     *
     * @param request that an authentication request is being created for
     * @param authRequest the authentication request object that should have its details
     * set
     */
    protected void setDetails(HttpServletRequest request, CompositeAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }
}
