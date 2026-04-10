package com.ale.venus.common.domain;

/**
 * 请求返回结果
 *
 * @param <T> 数据类型
 * @author Ale
 * @version 1.0.0
 */
public interface Result<T> {

    /**
     * 成功响应码
     */
    int SUCCESS_CODE = 200;

    /**
     * 失败响应码
     */
    int FAIL_CODE = 500;

    /**
     * 通用成功提示
     */
    String GENERIC_SUCCESS_MSG = "操作成功";

    /**
     * 通用失败提示
     */
    String GENERIC_FAILURE_MSG = "操作失败";

    /**
     * 获取处理的响应码
     *
     * @return 结果码
     */
    int getCode();

    /**
     * 获取结果信息
     *
     * @return 结果信息字符串
     */
    String getMessage();

    /**
     * 需要返回的数据
     *
     * @return 数据
     */
    T getData();

    /**
     * 是否成功
     *
     * @return true-成功，false-失败
     */
    boolean isSuccess();
}
