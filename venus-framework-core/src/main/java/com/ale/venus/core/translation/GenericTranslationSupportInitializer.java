package com.ale.venus.core.translation;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * 通用翻译支持初始化器
 *
 * @author Ale
 * @version 1.0.0
 */
public class GenericTranslationSupportInitializer implements SmartInitializingSingleton {

    /**
     * 通用翻译器对象提供器
     */
    private final ObjectProvider<GenericTranslator> genericTranslatorObjectProvider;

    public GenericTranslationSupportInitializer(ObjectProvider<GenericTranslator> genericTranslatorObjectProvider) {
        this.genericTranslatorObjectProvider = genericTranslatorObjectProvider;
    }

    @Override
    public void afterSingletonsInstantiated() {
        try {
            MethodHandle methodHandle = MethodHandles.privateLookupIn(GenericTranslationSupport.class, MethodHandles.lookup())
                .findStatic(GenericTranslationSupport.class, "registerGenericTranslator", MethodType.methodType(void.class, GenericTranslator.class));
            for (GenericTranslator genericTranslator : this.genericTranslatorObjectProvider) {
                methodHandle.invoke(genericTranslator);
            }
        } catch (Throwable e) {
            throw new BeanInitializationException("初始化通用翻译支持失败");
        }
    }
}
