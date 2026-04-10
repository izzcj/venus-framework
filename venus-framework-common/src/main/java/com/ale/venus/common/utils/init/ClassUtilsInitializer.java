package com.ale.venus.common.utils.init;

import com.ale.venus.common.utils.ClassUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.jspecify.annotations.NonNull;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * Class工具类初始化器
 * 不能使用UtilsInitializationAutoConfiguration配置，需要获取ApplicationContext
 *
 * @author Ale
 * @version 1.0.0
 */
public class ClassUtilsInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
        try {
            MethodHandles.privateLookupIn(ClassUtils.class, MethodHandles.lookup())
                .findStatic(ClassUtils.class, "setApplicationContext", MethodType.methodType(void.class, ApplicationContext.class))
                .invoke(applicationContext);
        } catch (Throwable e) {
            throw new ApplicationContextException("初始化Class工具类失败", e);
        }
    }
}
