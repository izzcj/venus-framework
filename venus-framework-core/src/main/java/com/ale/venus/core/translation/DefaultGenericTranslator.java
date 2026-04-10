package com.ale.venus.core.translation;

import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * 默认的通用翻译器
 *
 * @author Ale
 * @version 1.0.0
 */
@RequiredArgsConstructor
public class DefaultGenericTranslator implements GenericTranslator {

    /**
     * 可缓存的翻译映射数据管理器
     */
    private final CacheableTranslationMappingDataManager cacheableTranslationMappingDataManager;

    @Override
    public boolean supports(String s) {
        return true;
    }

    @Override
    public String translate(String type, Map<String, Object> params, String value) {
        Map<String, String> mapping = this.cacheableTranslationMappingDataManager.load(type, params);

        return mapping.get(value);
    }
}
