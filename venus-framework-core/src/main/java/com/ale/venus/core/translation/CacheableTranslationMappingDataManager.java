package com.ale.venus.core.translation;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ale.venus.common.constants.StringConstants;
import com.ale.venus.common.support.PatchData;
import com.ale.venus.common.utils.RedisUtils;
import com.ale.venus.core.share.RedisSharedDataContext;
import com.ale.venus.core.share.SharedDataContextFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.event.EventListener;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 可缓存的翻译映射数据管理器
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
public class CacheableTranslationMappingDataManager {

    /**
     * 加载缓存锁Key前缀
     */
    private static final String LOADING_CACHE_LOCK_KEY_PREFIX = "genericTranslationCacheLoading:";

    /**
     * 通用数据翻译映射缓存Key
     */
    private static final String GENERIC_TRANSLATION_MAPPING_DATA_CACHE = "genericTranslationMappingDataCache";

    /**
     * 翻译映射数据缓存上下文
     */
    private final RedisSharedDataContext translationMappingDataCache;

    /**
     * 翻译映射加载器
     */
    private final ObjectProvider<GenericTranslationMappingDataLoader> translationMappingDataLoaders;

    public CacheableTranslationMappingDataManager(SharedDataContextFactory sharedObjectContextFactory, ObjectProvider<GenericTranslationMappingDataLoader> translationMappingDataLoaders) {
        this.translationMappingDataCache = sharedObjectContextFactory.createRedisSharedDataContext(GENERIC_TRANSLATION_MAPPING_DATA_CACHE);
        this.translationMappingDataLoaders = translationMappingDataLoaders;
        if (translationMappingDataLoaders != null) {
            for (GenericTranslationMappingDataLoader genericTranslationMappingDataLoader : translationMappingDataLoaders) {
                List<GenericTranslationMappingData> genericTranslationMappingDataList = genericTranslationMappingDataLoader.loadAll();
                if (CollectionUtil.isEmpty(genericTranslationMappingDataList)) {
                    continue;
                }
                for (GenericTranslationMappingData genericTranslationMappingData : genericTranslationMappingDataList) {
                    String cacheKey = this.buildCacheKey(genericTranslationMappingData.getType(), genericTranslationMappingData.getParams());
                    Map<String, String> mappingData = genericTranslationMappingData.getMappingData();
                    if (CollectionUtil.isNotEmpty(mappingData)) {
                        this.translationMappingDataCache.set(cacheKey, mappingData);
                    }
                }
            }
        }
    }

    /**
     * 加载翻译的映射数据
     *
     * @param type   数据类型
     * @param params 参数
     * @return 翻译映射数据
     */
    public Map<String, String> load(String type, Map<String, Object> params) {
        String cacheKey = this.buildCacheKey(type, params);
        Map<String, String> cachedMappingData = this.translationMappingDataCache.get(cacheKey);

        if (MapUtil.isNotEmpty(cachedMappingData)) {
            return cachedMappingData;
        }

        // 加载翻译映射
        return RedisUtils.doWithLock(LOADING_CACHE_LOCK_KEY_PREFIX + cacheKey, () -> {
            boolean processed = false;
            Map<String, String> mappingData = null;
            for (GenericTranslationMappingDataLoader genericTranslationMappingDataLoader : this.translationMappingDataLoaders) {
                if (genericTranslationMappingDataLoader.supports(type)) {
                    mappingData = genericTranslationMappingDataLoader.load(type, params);
                    processed = true;
                    break;
                }
            }

            if (!processed) {
                // 没有被处理
                log.warn("通用翻译映射数据加载失败，并未找到可以加载[{}]类型映射数据的 GenericTranslationMappingLoader，请检查", type);
                mappingData = Collections.emptyMap();
            }

            if (MapUtil.isEmpty(mappingData)) {
                return mappingData != null ? mappingData : Collections.emptyMap();
            }

            this.translationMappingDataCache.set(cacheKey, mappingData);
            return mappingData;
        });
    }

    /**
     * 处理映射数据更新事件
     *
     * @param event 事件对象
     */
    @EventListener(GenericTranslationMappingDataUpdateEvent.class)
    public void onTranslationMappingDataUpdate(GenericTranslationMappingDataUpdateEvent event) {
        String cacheKey = this.buildCacheKey(event.getType(), event.getParams());
        if (!event.isPatch()) {
            // 清空
            this.translationMappingDataCache.remove(cacheKey);
            return;
        }

        PatchData<Pair<String, String>> patchData = event.getPatchData();
        Map<String, String> mappingData = this.translationMappingDataCache.get(cacheKey);
        if (MapUtil.isNotEmpty(mappingData) && patchData != null) {
            for (Pair<String, String> pair : patchData.getAdded()) {
                mappingData.put(pair.getKey(), pair.getValue());
            }
            for (Pair<String, String> pair : patchData.getChanged()) {
                mappingData.put(pair.getKey(), pair.getValue());
            }
            for (Pair<String, String> pair : patchData.getRemoved()) {
                mappingData.remove(pair.getKey());
            }

            this.translationMappingDataCache.set(cacheKey, mappingData);
        }
    }

    /**
     * 构建缓存Key
     *
     * @param type   翻译类型
     * @param params 参数
     * @return 缓存Key
     */
    private String buildCacheKey(String type, Map<String, Object> params) {
        if (MapUtil.isEmpty(params)) {
            return type;
        }
        return type + StringConstants.COLON + params.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .filter(entry -> ObjectUtil.isNotEmpty(entry.getValue()))
            .map(entry -> entry.getKey() + StringConstants.EQUAL + Convert.toStr(entry.getValue()))
            .collect(Collectors.joining(StringConstants.AMPERSAND));
    }

}
