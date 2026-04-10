package com.ale.venus.core.translation;

import com.ale.venus.core.share.SharedDataContextFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 通用翻译自动配置类
 *
 * @author Ale
 * @version 1.0.0
 */
@AutoConfiguration
public class GenericTranslationAutoConfiguration {

    /**
     * 可缓存的翻译映射数据管理器 Bean
     *
     * @param sharedDataContextFactory      共享数据上下文工厂
     * @param translationMappingDataLoaders 翻译映射数据加载器
     * @return Bean
     */
    @Bean
    @ConditionalOnMissingBean(GenericTranslator.class)
    public CacheableTranslationMappingDataManager cacheableTranslationMappingLoadingService(SharedDataContextFactory sharedDataContextFactory, ObjectProvider<GenericTranslationMappingDataLoader> translationMappingDataLoaders) {
        return new CacheableTranslationMappingDataManager(sharedDataContextFactory, translationMappingDataLoaders);
    }

    /**
     * 默认通用数据翻译器
     *
     * @param translationMappingDataManager 映射数据加载Service
     * @return Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public GenericTranslator genericTranslator(CacheableTranslationMappingDataManager translationMappingDataManager) {
        return new DefaultGenericTranslator(translationMappingDataManager);
    }

    /**
     * 通用翻译处理类初始化器Bean
     *
     * @param genericTranslatorObjectProvider 通用翻译器对象提供器
     * @return Bean
     */
    @Bean
    public GenericTranslationSupportInitializer genericTranslationSupportInitializer(ObjectProvider<GenericTranslator> genericTranslatorObjectProvider) {
        return new GenericTranslationSupportInitializer(genericTranslatorObjectProvider);
    }

}
