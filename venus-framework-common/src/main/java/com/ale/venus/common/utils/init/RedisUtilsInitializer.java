package com.ale.venus.common.utils.init;

import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.utils.RedisUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.jspecify.annotations.NonNull;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * Redis工具类初始化器
 *
 * @author Ale
 * @version 1.0.0
 */
public class RedisUtilsInitializer implements BeanPostProcessor {

    @NonNull
    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (bean instanceof RedisTemplate && StrUtil.equals(beanName, "redisTemplate")) {
            try {
                MethodHandles.privateLookupIn(RedisUtils.class, MethodHandles.lookup())
                    .findStatic(RedisUtils.class, "setRedisTemplate", MethodType.methodType(void.class, RedisTemplate.class))
                    .invoke(bean);
            } catch (Throwable e) {
                throw new BeanInitializationException("初始化Redis工具类失败", e);
            }
        }

        if (bean instanceof StringRedisTemplate && StrUtil.equals(beanName, "stringRedisTemplate")) {
            try {
                MethodHandles.privateLookupIn(RedisUtils.class, MethodHandles.lookup())
                    .findStatic(RedisUtils.class, "setStringRedisTemplate", MethodType.methodType(void.class, StringRedisTemplate.class))
                    .invoke(bean);
            } catch (Throwable e) {
                throw new BeanInitializationException("初始化Redis工具类失败", e);
            }
        }

        return bean;
    }

}
