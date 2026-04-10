package com.ale.venus.common.logging;

import com.ale.venus.common.constants.StringConstants;
import com.ale.venus.common.security.AuthenticatedUser;
import com.ale.venus.common.utils.SecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.jspecify.annotations.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 请求信息MDC过滤器
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class LogRequestMdcFilter extends OncePerRequestFilter implements OrderedFilter {

    /**
     * 默认排序
     */
    private final int order;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        long beginTime = System.currentTimeMillis();
        AuthenticatedUser authenticatedUser = SecurityUtils.getLoginUser();
        try {
            filterChain.doFilter(request, response);
        } finally {
            // 打印请求耗时
            long elapsedTime = System.currentTimeMillis() - beginTime;
            if (authenticatedUser != null) {
                log.info(
                    "[{}][{}][{}]: {}ms",
                    AnsiOutput.toString(AnsiColor.BRIGHT_YELLOW, authenticatedUser.getName(), StringConstants.COLON, authenticatedUser.getId()),
                    AnsiOutput.toString(AnsiColor.BRIGHT_CYAN, request.getMethod(), StringConstants.SPACE, request.getRequestURI()),
                    AnsiOutput.toString(AnsiColor.BRIGHT_BLUE, request.getRemoteAddr()),
                    AnsiOutput.toString(
                        elapsedTime > 3000 ? AnsiColor.BRIGHT_YELLOW : AnsiColor.GREEN,
                        elapsedTime
                    )
                );
            } else {
                log.info(
                    "[{}][{}]: {}ms",
                    AnsiOutput.toString(AnsiColor.BRIGHT_CYAN, request.getMethod(), StringConstants.SPACE, request.getRequestURI()),
                    AnsiOutput.toString(AnsiColor.BRIGHT_BLUE, request.getRemoteAddr()),
                    AnsiOutput.toString(
                        elapsedTime > 3000 ? AnsiColor.BRIGHT_YELLOW : AnsiColor.GREEN,
                        elapsedTime
                    )
                );
            }
            MDC.clear();
        }
    }

    @Override
    public int getOrder() {
        return order;
    }
}
