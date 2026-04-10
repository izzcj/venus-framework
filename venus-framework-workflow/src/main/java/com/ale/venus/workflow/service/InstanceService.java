package com.ale.venus.workflow.service;


import com.ale.venus.workflow.entity.FlowInstance;
import com.ale.venus.workflow.model.node.FlowNode;
import com.ale.venus.workflow.support.StartInstanceParam;

import java.util.Map;

/**
 * 流程实例服务
 *
 * @author Ale
 * @version 1.0.0
 */
public interface InstanceService {

    /**
     * 启动流程实例
     *
     * @param startInstanceParam 启动流程参数
     * @return 流程实例
     */
    FlowInstance startInstance(StartInstanceParam startInstanceParam);

    /**
     * 重启流程实例
     *
     * @param instanceId 流程实例ID
     * @param variable   流程变量
     * @param starter    发起人
     * @return 新流程实例
     */
    FlowInstance restartInstance(String instanceId, Map<String, Object> variable, String starter);

    /**
     * 更新流程实例
     *
     * @param flowInstance 流程实例
     * @return 结果
     */
    boolean updateInstance(FlowInstance flowInstance);

    /**
     * 添加流程实例变量
     *
     * @param instanceId 流程实例ID
     * @param variable   变量
     * @return 结果
     */
    boolean addVariable(String instanceId, Map<String, Object> variable);

    /**
     * 追加流程节点
     * 插入节点parentId之后
     *
     * @param instanceId   流程实例ID
     * @param flowNode 流程节点
     * @return 结果
     */
    boolean appendNode(String instanceId, FlowNode flowNode);

    /**
     * 结束流程实例（正常结束）
     *
     * @param instanceId 流程实例ID
     * @return 是否完成成功
     */
    default boolean finish(String instanceId) {
        return this.finish(instanceId, false);
    }

    /**
     * 结束流程实例（正常结束）
     *
     * @param instanceId 流程实例ID
     * @param isAuto     是否自动通过
     * @return 结束结果
     */
    boolean finish(String instanceId, boolean isAuto);

    /**
     * 驳回流程实例
     *
     * @param instanceId 流程实例ID
     * @return 是否驳回成功
     */
    default boolean reject(String instanceId) {
        return this.reject(instanceId, false);
    }

    /**
     * 驳回流程实例
     *
     * @param instanceId 流程实例ID
     * @param isAuto     是否自动驳回
     * @return 驳回结果
     */
    boolean reject(String instanceId, boolean isAuto);

    /**
     * 撤销流程实例
     *
     * @param instanceId 流程实例ID
     * @return 撤销结果
     */
    boolean revoke(String instanceId);

    /**
     * 流程实例超时处理
     *
     * @param instanceId 流程实例ID
     * @return 超时处理结果
     */
    boolean timeout(String instanceId);

    /**
     * 校验流程实例是否允许执行
     *
     * @param instanceId 流程实例ID
     */
    void checkInstanceAllowExecution(String instanceId);
}
