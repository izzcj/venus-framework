package com.ale.venus.workflow.support;

import com.ale.venus.common.domain.entity.BaseEntity;
import com.ale.venus.common.domain.entity.EntityInitializer;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 流程业务实体持有器
 *
 * @author Ale
 * @version 1.0.0
 */
public class FlowBusinessEntityHolder implements EntityInitializer {

    /**
     * 流程业务实体映射
     */
    private static final Map<Class<? extends BaseEntity>, FlowBusinessEntity> FLOW_BUSINESS_ENTITY_MAP = Maps.newHashMap();

    @Override
    public void initialize(Class<? extends BaseEntity> entityClass) {
        FlowBusinessEntity flowBusinessEntity = entityClass.getAnnotation(FlowBusinessEntity.class);
        if (flowBusinessEntity != null) {
            FLOW_BUSINESS_ENTITY_MAP.put(entityClass, flowBusinessEntity);
        }
    }

    /**
     * 获取流程业务实体注解信息
     *
     * @param entityClass 实体类
     * @return 流程业务实体注解信息
     */
    public static FlowBusinessEntity getFlowBusinessEntity(Class<? extends BaseEntity> entityClass) {
        return FLOW_BUSINESS_ENTITY_MAP.get(entityClass);
    }

    /**
     * 获取流程业务实体映射
     *
     * @return 流程业务实体映射
     */
    public static Map<Class<? extends BaseEntity>, FlowBusinessEntity> getFlowBusinessEntityMap() {
        return FLOW_BUSINESS_ENTITY_MAP;
    }
}
