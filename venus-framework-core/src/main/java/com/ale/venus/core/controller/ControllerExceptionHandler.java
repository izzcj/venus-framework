package com.ale.venus.core.controller;

import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.domain.JsonResult;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.util.stream.Collectors;

/**
 * Controller异常处理器
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * 响应错误结果信息
     *
     * @param headers 响应头信息
     * @param message 错误消息
     * @param args 消息模板参数
     * @return 响应实体对象
     */
    private ResponseEntity<Object> response(HttpHeaders headers, String message, Object... args) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .headers(headers)
            .body(JsonResult.fail(message, args));
    }

    /**
     * 请求缺少Path变量异常处理
     */
    @NonNull
    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(@NonNull MissingPathVariableException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        log.warn("请求缺少Path变量，名称：{}，类型：{}", ex.getVariableName(), ex.getParameter().getNestedParameterType().getName());
        return this.response(headers, "请求缺少Path变量[{}]，请检查", ex.getVariableName());
    }

    /**
     * 请求缺少参数异常处理
     */
    @NonNull
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(@NonNull MissingServletRequestParameterException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        log.warn("请求缺少参数，名称：{}，类型：{}", ex.getParameterName(), ex.getParameterType());
        return this.response(headers, "请求缺少参数[{}]，请检查", ex.getParameterName());
    }

    /**
     * 请求缺少Part参数异常处理
     */
    @NonNull
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(@NonNull MissingServletRequestPartException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        log.warn("请求缺少Part参数[{}]", ex.getRequestPartName());
        return this.response(headers, "请求缺少Part参数[{}]，请检查", ex.getRequestPartName());
    }

    /**
     * 请求出现servlet binding异常处理
     */
    @NonNull
    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(@NonNull ServletRequestBindingException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        log.warn("ServletBinding异常：{}", ex.getMessage());
        return this.response(headers, "请求出现ServletBinding异常，请检查");
    }

    /**
     * 请求出现参数转换异常处理
     */
    @NonNull
    @Override
    protected ResponseEntity<Object> handleConversionNotSupported(@NonNull ConversionNotSupportedException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        log.warn("参数转换异常，无法将类型为[{}]的参数[{}]转为指定的类型[{}]", ex.getValue().getClass().getName(), ex.getPropertyName(), ex.getRequiredType());
        return this.response(headers, "请求出现参数转换异常，请检查");
    }

    /**
     * 请求出现参数类型不匹配异常处理
     */
    @NonNull
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(@NonNull TypeMismatchException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        log.warn("参数类型不匹配，参数[{}]要求的类型为[{}]，实际类型为：[{}]", ex.getPropertyName(), ex.getRequiredType(), ex.getValue().getClass().getName());
        return this.response(headers, "请求出现参数类型不匹配异常，请检查");
    }

    /**
     * 请求数据无法读取异常处理
     */
    @NonNull
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        log.warn("读取请求数据发生异常：{}", ex.getMessage());
        return this.response(headers, StrUtil.startWith(ex.getMessage(), "Required request body is missing") ? "请求未传Body体数据，请检查" : "读取请求数据出现异常，请检查");
    }

    /**
     * 响应请求异常处理
     */
    @NonNull
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(@NonNull HttpMessageNotWritableException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        log.warn("响应请求结果发生异常：{}", ex.getMessage());
        return this.response(headers, "响应请求结果出现异常，请检查");
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        MethodParameter parameter = ex.getParameter();
        BindingResult bindingResult = ex.getBindingResult();
        String message = bindingResult.getAllErrors().stream().map(objectError -> objectError.getObjectName() + "：" + objectError.getDefaultMessage()).collect(Collectors.joining("，"));
        log.warn("请求参数[{}({})]校验不通过，详细校验失败字段[{}]", parameter.getParameterName(), parameter.getParameterType().getName(), message);

        if (bindingResult.hasFieldErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String name = fieldError.getField();

            return this.response(headers, "参数校验不通过：" + name + StringPool.COLON + fieldError.getDefaultMessage());
        } else {
            return this.response(headers, "参数校验不通过：" + bindingResult.getAllErrors().getFirst().getDefaultMessage());
        }
    }

    /**
     * 404异常处理
     */
    @NonNull
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(@NonNull NoHandlerFoundException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        log.warn("响应请求[{} - {}]接口地址不存在", ex.getHttpMethod(), ex.getRequestURL());
        return this.response(headers, "请求地址不存在");
    }

    /**
     * 异步请求超时异常处理
     */
    @NonNull
    @Override
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(@NonNull AsyncRequestTimeoutException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest webRequest) {
        log.warn("异步请求超时：{}", ex.getMessage());
        return this.response(headers, "异步请求超时，请检查");
    }
}
