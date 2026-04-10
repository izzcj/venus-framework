package com.ale.venus.core.spring;

import com.ale.venus.core.spring.converter.ComponentScanMark;
import com.google.common.collect.Lists;
import org.slf4j.MDC;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.task.TaskDecorator;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.ResourceRegionHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;
import java.util.Map;

/**
 * Spring拓展自动配置
 *
 * @author Ale
 * @version 1.0.0
 */
@EnableAsync
@EnableScheduling
@EnableAspectJAutoProxy(exposeProxy = true)
@AutoConfiguration
@ComponentScan(basePackageClasses = ComponentScanMark.class)
@AutoConfigureBefore({HttpMessageConvertersAutoConfiguration.class, WebMvcAutoConfiguration.class, WebFluxAutoConfiguration.class, TaskExecutionAutoConfiguration.class})
public class SpringExtensionAutoConfiguration {

    /**
     * 自定义Http消息转换器
     *
     * @param converters 消息转换器Bean
     * @return 消息转换器聚合器
     */
    @Bean
    public HttpMessageConverters httpMessageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
        List<HttpMessageConverter<?>> messageConverters = Lists.newArrayList();
        // 添加默认httpMessageConverters
        messageConverters.add(new ByteArrayHttpMessageConverter());
        messageConverters.add(new ResourceHttpMessageConverter());
        messageConverters.add(new ResourceRegionHttpMessageConverter());
        try {
            messageConverters.add(new SourceHttpMessageConverter<>());
        } catch (Throwable ignored) {
            // Ignore when no TransformerFactory implementation is available...
        }
        messageConverters.add(new AllEncompassingFormHttpMessageConverter());
        messageConverters.addAll(converters.orderedStream().toList());
        return new HttpMessageConverters(false, messageConverters);
    }

    /**
     * RestTemplate Bean
     *
     * @param restTemplateBuilder RestTemplate构建器
     * @return RestTemplate Bean
     */
    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    /**
     * WebMvc配置器Bean
     *
     * @param converterFactories               转换工厂
     * @param handlerMethodArgumentResolvers   参数解析器
     * @param handlerMethodReturnValueHandlers 结果处理器
     * @param handlerInterceptors              处理器拦截器
     * @return 配置器Bean
     */
    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public WebMvcConfigurer venusWebMvcConfigurer(ObjectProvider<ConverterFactory<?, ?>> converterFactories,
                                                  ObjectProvider<HandlerMethodArgumentResolver> handlerMethodArgumentResolvers,
                                                  ObjectProvider<HandlerMethodReturnValueHandler> handlerMethodReturnValueHandlers,
                                                  ObjectProvider<HandlerInterceptor> handlerInterceptors) {
        return new VenusWebMvcConfigurer(
            converterFactories,
            handlerMethodArgumentResolvers,
            handlerMethodReturnValueHandlers,
            handlerInterceptors
        );
    }

    /**
     * 任务装饰器Bean
     *
     * @return 任务装饰器Bean
     */
    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public TaskDecorator taskDecorator() {
        return runnable -> {
            SecurityContext securityContext = SecurityContextHolder.getContext() != null ? SecurityContextHolder.getContext() : SecurityContextHolder.createEmptyContext();
            Map<String, String> contextMap = MDC.getCopyOfContextMap();

            return () -> {
                try {
                    SecurityContextHolder.setContext(securityContext);
                    if (contextMap != null) {
                        MDC.setContextMap(contextMap);
                    }
                    runnable.run();
                } finally {
                    SecurityContextHolder.clearContext();
                    MDC.clear();
                }
            };
        };
    }
}
