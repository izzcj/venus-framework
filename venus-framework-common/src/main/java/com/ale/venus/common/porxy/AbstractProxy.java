package com.ale.venus.common.porxy;

import com.ale.venus.common.porxy.invoker.ProxyMethodInvoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 抽象代理类
 *
 * @param <T> 原始对象类型
 * @author Ale
 * @version 1.0.0
 */
public abstract class AbstractProxy<T> implements VenusProxy<T> {

    /**
     * 原始对象
     */
    protected T originalObject;

    /**
     * 代理方法回调器
     */
    protected ProxyMethodInvoker<T> proxyMethodInvoker;

    @Override
    public Object createProxy(T originalObject, ProxyMethodInvoker<T> proxyMethodInvoker) {
        this.originalObject = originalObject;
        this.proxyMethodInvoker = proxyMethodInvoker;
        return this.create(originalObject);
    }

    /**
     * 创建代理
     *
     * @param originalObject 原始对象
     * @return 代理对象
     */
    protected abstract Object create(T originalObject);

    /**
     * 代理方法回调
     *
     * @param method     方法
     * @param args       参数
     * @return 方法返回值
     * @throws Throwable 方法执行异常
     */
    protected Object methodInvoke(Method method, Object[] args) throws Throwable {
        if (this.proxyMethodInvoker == null) {
            return method.invoke(this.originalObject, args);
        }

        Object result;

        try {
            this.proxyMethodInvoker.before(this, this.originalObject, method, args);

            result = method.invoke(this.originalObject, args);

            this.proxyMethodInvoker.after(this, this.originalObject, method, args, result);

            return result;
        } catch (InvocationTargetException e) {
            this.proxyMethodInvoker.afterException(this, this.originalObject, method, args, e.getTargetException());
            throw e.getTargetException();
        }
    }
}
