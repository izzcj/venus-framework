package com.ale.venus.core.translation;

import com.ale.venus.common.support.Supportable;

import java.util.List;
import java.util.Map;

/**
 * 通用翻译映射数据加载器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface GenericTranslationMappingDataLoader extends Supportable<String> {

    /**
     * 加载补丁翻译映射数据
     *
     * @param type   数据类型
     * @param params 参数
     * @return 通用翻译映射数据
     */
    Map<String, String> load(String type, Map<String, Object> params);

    /**
     * 加载所有翻译映射数据
     *
     * @return 通用翻译映射数据
     */
    List<GenericTranslationMappingData> loadAll();
}
