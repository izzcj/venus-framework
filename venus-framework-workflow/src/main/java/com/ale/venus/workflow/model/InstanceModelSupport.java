package com.ale.venus.workflow.model;

import com.ale.venus.common.utils.CastUtils;
import com.ale.venus.workflow.cache.FlowEngineCache;
import com.ale.venus.workflow.entity.FlowInstance;
import com.ale.venus.workflow.parser.InstanceModelParser;
import com.ale.venus.workflow.support.FlowContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 流程实例模型支持
 *
 * @author Ale
 * @version 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InstanceModelSupport {

    /**
     * 流程定义解析器
     */
    private static InstanceModelParser instanceModelParser;

    /**
     * 流程引擎缓存
     */
    private static FlowEngineCache flowEngineCache;

    /**
     * 设置流程实例解析器
     *
     * @param instanceModelParser 流程定义解析器
     */
    static void setInstanceModelParser(InstanceModelParser instanceModelParser) {
        InstanceModelSupport.instanceModelParser = instanceModelParser;
    }

    /**
     * 设置流程引擎缓存
     *
     * @param flowEngineCache 流程引擎缓存
     */
    static void setFlowEngineCache(FlowEngineCache flowEngineCache) {
        InstanceModelSupport.flowEngineCache = flowEngineCache;
    }

    /**
     * 解析流程实例模型
     *
     * @param id 流程实例ID
     * @return 流程实例
     */
    public static InstanceModel parseInstanceModel(String id) {
        return parseInstanceModel(id, true);
    }

    /**
     * 解析流程实例模型
     *
     * @param id       流程实例ID
     * @param useCache 是否使用缓存
     * @return 流程实例
     */
    public static InstanceModel parseInstanceModel(String id, boolean useCache) {
        if (useCache) {
            InstanceModel instanceModel = CastUtils.cast(flowEngineCache.get(id));
            if (instanceModel == null) {
                instanceModel = parseInstanceModel(FlowContext.getQueryService().fetchInstanceById(id));
                flowEngineCache.set(id, instanceModel);
            }
            return instanceModel;
        }
        return parseInstanceModel(FlowContext.getQueryService().fetchInstanceById(id));
    }

    /**
     * 解析流程实例模型
     *
     * @param flowInstance 流程实例
     * @return 流程实例
     */
    public static InstanceModel parseInstanceModel(FlowInstance flowInstance) {
        return parseInstanceModel(flowInstance, true);
    }

    /**
     * 解析流程实例模型
     *
     * @param flowInstance 流程实例
     * @param useCache     是否使用缓存
     * @return 流程实例
     */
    public static InstanceModel parseInstanceModel(FlowInstance flowInstance, boolean useCache) {
        if (useCache) {
            InstanceModel instanceModel = CastUtils.cast(flowEngineCache.get(flowInstance.getId()));
            if (instanceModel == null) {
                instanceModel = instanceModelParser.parse(flowInstance);
                flowEngineCache.set(flowInstance.getId(), instanceModel);
            }
            return instanceModel;
        }
        return instanceModelParser.parse(flowInstance);
    }

    /**
     * 失效缓存
     *
     * @param instanceId 流程实例ID
     */
    public static void invalidationCache(String instanceId) {
        flowEngineCache.remove(instanceId);
    }
}
