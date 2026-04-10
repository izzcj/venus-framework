package com.ale.venus.core.translation;

import cn.hutool.core.lang.Pair;
import com.ale.venus.common.support.PatchData;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * 通用翻译映射数据更新事件
 *
 * @author Ale
 * @version 1.0.0
 */
@Getter
public class GenericTranslationMappingDataUpdateEvent extends ApplicationEvent {

    /**
     * 数据类型
     */
    private final String type;

    /**
     * 是否补丁数据映射，不进行全量删除
     */
    private final boolean patch;

    /**
     * 参数
     */
    private Map<String, Object> params;

    /**
     * 补丁数据
     */
    private PatchData<Pair<String, String>> patchData;

    public GenericTranslationMappingDataUpdateEvent(String type) {
        super(type);
        this.type = type;
        this.patch = false;
    }

    public GenericTranslationMappingDataUpdateEvent(String type, Map<String, Object> params) {
        super(type);
        this.type = type;
        this.patch = false;
        this.params = params;
    }

    public GenericTranslationMappingDataUpdateEvent(String type, PatchData<Pair<String, String>> patchData) {
        super(type);
        this.type = type;
        this.patch = true;
        this.patchData = patchData;
    }

    public GenericTranslationMappingDataUpdateEvent(String type, Map<String, Object> params, PatchData<Pair<String, String>> patchData) {
        super(type);
        this.type = type;
        this.patch = true;
        this.params = params;
        this.patchData = patchData;
    }
}
