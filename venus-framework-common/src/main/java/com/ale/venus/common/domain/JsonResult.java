package com.ale.venus.common.domain;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.ArrayUtil;
import com.ale.venus.common.exception.BadResultCodeException;
import com.ale.venus.common.exception.ExceptionCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * json类型返回结果
 *
 * @param <T> 数据类型
 * @author Ale
 * @version 1.0.0
 */
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class JsonResult<T> implements Result<T> {

    /**
     * 响应码
     */
    private int code;
    /**
     * 信息
     */
    private String message;
    /**
     * 数据
     */
    private T data;

    /**
     * 获取处理的响应码
     *
     * @return 结果码
     */
    @Override
    public int getCode() {
        return this.code;
    }

    /**
     * 获取结果信息
     *
     * @return 结果信息字符串
     */
    @Override
    public String getMessage() {
        return this.message;
    }

    /**
     * 需要返回的数据
     *
     * @return 数据
     */
    @Override
    public T getData() {
        return this.data;
    }

    /**
     * 是否成功
     *
     * @return bool
     */
    @Override
    public boolean isSuccess() {
        return SUCCESS_CODE == this.code;
    }

    /**
     * 成功结果
     *
     * @param message 消息
     * @param data    数据
     *
     * @return 结果对象
     * @param <T> 数据类型
     */
    public static <T> JsonResult<T> success(String message, T data) {
        return new JsonResult<>(SUCCESS_CODE, message, data);
    }

    /**
     * 成功结果
     *
     * @param message 消息
     *
     * @return 结果对象
     * @param <T> 数据类型
     */
    public static <T> JsonResult<T> success(String message) {
        return success(message, (T) null);
    }

    /**
     * 成功结果
     *
     * @param message 消息
     * @param data    数据
     * @param args    格式化参数
     *
     * @return 结果对象
     * @param <T> 数据类型
     */
    public static <T> JsonResult<T> success(String message, T data, Object... args) {
        return new JsonResult<>(SUCCESS_CODE, StrFormatter.format(message, args), data);
    }

    /**
     * 成功结果
     *
     * @param message 消息
     * @param args    格式化参数
     *
     * @return 结果对象
     * @param <T> 数据类型
     */
    public static <T> JsonResult<T> success(String message, Object... args) {
        return success(StrFormatter.format(message, args), (T) null);
    }

    /**
     * 成功结果
     *
     * @param data 数据
     *
     * @return 结果对象
     * @param <T> 数据类型
     */
    public static <T> JsonResult<T> success(T data) {
        return success(GENERIC_SUCCESS_MSG, data);
    }

    /**
     * 成功结果
     *
     * @return 结果对象
     * @param <T> 数据类型
     */
    public static <T> JsonResult<T> success() {
        return success(GENERIC_SUCCESS_MSG);
    }


    /**
     * 失败结果
     *
     * @param exceptionCode 错误枚举
     *
     * @return 结果对象
     * @param <T> 数据类型
     */
    public static <T> JsonResult<T> fail(ExceptionCode exceptionCode) {
        checkCode(exceptionCode.getCode());
        return new JsonResult<>(exceptionCode.getCode(), exceptionCode.getMsg(), null);
    }

    /**
     * 失败结果
     *
     * @param code    响应码
     * @param message 失败消息
     *
     * @return 结果对象
     * @param <T> 数据类型
     */
    public static <T> JsonResult<T> fail(int code, String message) {
        checkCode(code);
        return new JsonResult<>(code, message, null);
    }

    /**
     * 失败结果
     *
     * @param code    响应码
     * @param message 失败消息
     * @param data    数据
     *
     * @return 结果对象
     * @param <T> 数据类型
     */
    public static <T> JsonResult<T> failWithData(int code, String message, T data) {
        checkCode(code);
        return new JsonResult<>(code, message, data);
    }

    /**
     * 失败结果
     *
     * @param message 失败消息
     *
     * @return 结果对象
     * @param <T> 数据类型
     */
    public static <T> JsonResult<T> fail(String message) {
        return fail(FAIL_CODE, message);
    }

    /**
     * 失败结果
     *
     * @param code    响应码
     * @param message 失败消息
     * @param args    格式化参数
     *
     * @return 结果对象
     * @param <T> 数据类型
     */
    public static <T> JsonResult<T> fail(int code, String message, Object... args) {
        checkCode(code);
        return new JsonResult<>(code, StrFormatter.format(message, args), null);
    }

    /**
     * 失败结果
     *
     * @param message 失败消息
     * @param args    格式化参数
     *
     * @return 结果对象
     * @param <T> 数据类型
     */
    public static <T> JsonResult<T> fail(String message, Object... args) {
        if (ArrayUtil.isEmpty(args)) {
            return fail(FAIL_CODE, message);
        }

        return fail(FAIL_CODE, StrFormatter.format(message, args));
    }

    /**
     * 失败结果
     *
     * @return 结果对象
     * @param <T> 数据类型
     */
    public static <T> JsonResult<T> fail() {
        return fail(GENERIC_FAILURE_MSG);
    }

    /**
     * 检查响应码
     *
     * @param code 结果码
     */
    private static void checkCode(int code) {
        if (code <= 0) {
            throw new BadResultCodeException("错误响应码必须大于0");
        }
    }
}
