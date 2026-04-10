package com.ale.venus.workflow.entity;

import com.ale.venus.workflow.enumeration.FlowExecutionState;
import com.ale.venus.workflow.model.node.FlowNode;
import com.ale.venus.workflow.support.IdGeneratorSupport;
import com.ale.venus.workflow.support.JsonVariableAccessor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 流程执行记录
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
public class FlowExecution implements JsonVariableAccessor, Serializable {

    /**
     * id
     */
    private String id;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 流程实例ID
     */
    private String instanceId;

    /**
     * 父级ID
     */
    private String parentId;

    /**
     * 上次执行记录ID
     */
    private String lastExecutionId;

    /**
     * 节点ID
     */
    private String nodeId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点类型
     */
    private String nodeType;

    /**
     * 状态
     *
     * @see FlowExecutionState
     */
    private String state;

    /**
     * 完成时间
     */
    private LocalDateTime completedTime;

    /**
     * 变量
     */
    private String variable;

    /**
     * 删除状态
     */
    private Boolean deleted;

    /**
     * 创建流程执行记录
     *
     * @param instance        流程实例
     * @param node            执行节点
     * @param lastExecutionId 上次执行记录ID
     * @param parentId        父级ID
     * @param variable        变量
     * @return 流程执行记录
     */
    public static FlowExecution of(FlowInstance instance, FlowNode node, String lastExecutionId, String parentId, Map<String, Object> variable) {
        FlowExecution execution = new FlowExecution();
        execution.setId(IdGeneratorSupport.generateId());
        execution.setCreatedAt(LocalDateTime.now());
        execution.setInstanceId(instance.getId());
        execution.setLastExecutionId(lastExecutionId);
        execution.setParentId(parentId);
        execution.setState(FlowExecutionState.ACTIVE.getValue());
        execution.setNodeId(node.getId());
        execution.setNodeName(node.getName());
        execution.setNodeType(node.getType());
        execution.addVariable(variable);
        execution.setDeleted(false);
        return execution;
    }
}
