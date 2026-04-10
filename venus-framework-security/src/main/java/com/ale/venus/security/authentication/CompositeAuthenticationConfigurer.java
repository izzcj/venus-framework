package com.ale.venus.security.authentication;

import com.ale.venus.security.contanst.SecurityConstants;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * 聚合认证配置器
 *
 * @param <H> HttpSecurityBuilder
 * @author Ale
 * @version 1.0.0
 */
public class CompositeAuthenticationConfigurer<H extends HttpSecurityBuilder<H>> extends
    AbstractAuthenticationFilterConfigurer<H, CompositeAuthenticationConfigurer<H>, CompositeAuthenticationFilter> {

    /**
     * Creates a new instance
     */
    public CompositeAuthenticationConfigurer() {
        super(new CompositeAuthenticationFilter(), SecurityConstants.LOGIN_URI);
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, loginProcessingUrl);
    }
}
