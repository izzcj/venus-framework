package com.ale.venus.common.porxy;

import com.ale.venus.common.utils.CastUtils;
import org.springframework.aop.framework.AopContext;

/**
 * 代理实例解析能力
 *
 * @param <T> 代理实例类型
 * @author Ale
 */
public interface ProxyResolvable<T> {

    /**
     * 解析代理对象
     *
     * @return 代理对象
     */
    default T resolveProxy() {
        return CastUtils.cast(AopContext.currentProxy());
    }

}
