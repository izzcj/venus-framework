package com.ale.venus.security.support;

import java.util.List;

/**
 * Mvc路径模式提供器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface MvcPatternProvider {

    /**
     * 提供Mvc路径模式
     *
     * @return Mvc路径模式列表
     */
    List<MvcPattern> provide();
}
