package com.ale.venus.common.utils;

import cn.hutool.core.io.IoUtil;
import com.ale.venus.common.exception.UtilException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * Servlet工具类
 *
 * @author Ale
 * @version 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServletUtils {

    /**
     * 获取Servlet请求对象
     *
     * @return 请求对象
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            // 不存在请求对象，说明并非请求进来
            throw new UtilException("当前线程不存在Servlet请求对象");
        }

        return requestAttributes.getRequest();
    }

    /**
     * 获取Servlet响应对象
     *
     * @return 响应对象
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            // 不存在请求对象，说明并非请求进来
            throw new UtilException("当前线程不存在Servlet响应对象");
        }

        return requestAttributes.getResponse();
    }

    /**
     * 响应信息
     *
     * @param response 响应对象
     * @param status   状态码
     * @param data     数据
     * @throws IOException 异常
     */
    public static void responseJson(HttpServletResponse response, HttpStatus status, Object data) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        response.getWriter().write(JsonUtils.toJson(data));
        response.flushBuffer();
    }

    /**
     * 下载文件
     *
     * @param response    响应对象
     * @param filename    文件名称
     * @param inputStream 输入流
     */
    public static void responseFile(HttpServletResponse response, String filename, InputStream inputStream) {
        try {
            response.setContentType("application/x-download");
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
            IoUtil.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new UtilException("响应文件出现错误：{}", e, e.getMessage());
        }
    }

    /**
     * 下载文件
     *
     * @param response     响应对象
     * @param filename     文件名称
     * @param streamWriter 流写入函数
     */
    public static void responseFile(HttpServletResponse response, String filename, Consumer<OutputStream> streamWriter) {
        try {
            response.setContentType("application/x-download");
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
            var bos = new ByteArrayOutputStream();
            streamWriter.accept(bos);
            bos.writeTo(response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new UtilException("响应文件出现错误：{}", e, e.getMessage());
        }
    }

}
