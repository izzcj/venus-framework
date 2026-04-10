package com.ale.venus.security.filter;

import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.constants.StringConstants;
import com.ale.venus.common.domain.JsonResult;
import com.ale.venus.common.utils.CacheUtils;
import com.ale.venus.common.utils.RedisUtils;
import com.ale.venus.common.utils.ServletUtils;
import com.ale.venus.security.contanst.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

/**
 * 登录频率限制过滤器
 *
 * @author Ale
 * @version 1.0.0
 */
public class LoginRateLimitFilter extends OncePerRequestFilter {

    /**
     * 登录频率限制Key前缀
     */
    private static final String LOGIN_RATE_LIMIT_KEY_PREFIX = CacheUtils.buildCacheKeyWithPrefix("loginRateLimit");

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        if (requestUri.endsWith(StringConstants.SLASH)) {
            requestUri = StrUtil.strip(requestUri, null, StringConstants.SLASH);
        }

        if (!StrUtil.equals(requestUri, SecurityConstants.LOGIN_URI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 登录请求
        String cacheKey = CacheUtils.buildCacheKey(
            LOGIN_RATE_LIMIT_KEY_PREFIX,
            request.getRemoteAddr()
        );

        int count = Integer.parseInt(
            RedisUtils.computeString(cacheKey, f -> StringConstants.ONE, Duration.ofMinutes(30))
        );
        if (count >= 10) {
            // 次数已经超限
            ServletUtils.responseJson(
                response,
                HttpStatus.OK,
                JsonResult.fail("登录次数过于频繁，请稍后再试")
            );
            return;
        } else {
            RedisUtils.increment(cacheKey);
        }

        filterChain.doFilter(request, response);
    }

}
