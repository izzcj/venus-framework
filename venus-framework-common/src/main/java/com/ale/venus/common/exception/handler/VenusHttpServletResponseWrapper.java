package com.ale.venus.common.exception.handler;

import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.domain.JsonResult;
import com.ale.venus.common.exception.ExceptionCode;
import com.ale.venus.common.utils.ServletUtils;
import io.undertow.util.StatusCodes;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.http.HttpStatus;

import java.io.IOException;

/**
 * Venus Http响应对象包装器
 *
 * @author Ale
 * @version 1.0.0
 */
public class VenusHttpServletResponseWrapper extends HttpServletResponseWrapper {

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response the {@link HttpServletResponse} to be wrapped.
     * @throws IllegalArgumentException if the response is null
     */
    public VenusHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void sendError(int sc) throws IOException {
        this.sendError(sc, StatusCodes.getReason(sc));
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        ServletUtils.responseJson(
            this,
            HttpStatus.INTERNAL_SERVER_ERROR,
            JsonResult.fail(ExceptionCode.DEFAULT_ERROR.getCode(), "服务器异常：{}", StrUtil.blankToDefault(msg, String.valueOf(sc)))
        );
    }
}
