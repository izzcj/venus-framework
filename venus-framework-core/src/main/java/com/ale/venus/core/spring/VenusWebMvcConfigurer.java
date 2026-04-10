package com.ale.venus.core.spring;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * WebMvc配置
 *
 * @author Ale
 * @version 1.0.0
 */
@RequiredArgsConstructor
public class VenusWebMvcConfigurer implements WebMvcConfigurer {

    /**
     * 转换器工厂
     */
    private final ObjectProvider<ConverterFactory<?, ?>> converterFactories;

    /**
     * 参数处理器
     */
    private final ObjectProvider<HandlerMethodArgumentResolver> handlerMethodArgumentResolvers;

    /**
     * 返回结果处理器
     */
    private final ObjectProvider<HandlerMethodReturnValueHandler> handlerMethodReturnValueHandlers;

    /**
     * 处理器拦截器
     */
    private final ObjectProvider<HandlerInterceptor> handlerInterceptors;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        this.handlerInterceptors.orderedStream().forEach(registry::addInterceptor);
    }

    @Override
    public void addFormatters(@NonNull FormatterRegistry registry) {
        this.converterFactories.orderedStream().forEach(registry::addConverterFactory);
    }

    @Override
    public void addArgumentResolvers(@NonNull List<HandlerMethodArgumentResolver> resolvers) {
        this.handlerMethodArgumentResolvers.orderedStream().forEach(resolvers::add);
    }

    @Override
    public void addReturnValueHandlers(@NonNull List<HandlerMethodReturnValueHandler> handlers) {
        this.handlerMethodReturnValueHandlers.orderedStream().forEach(handlers::add);
    }

    @Override
    public void configureContentNegotiation(@NonNull ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }
}
