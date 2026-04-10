package com.ale.venus.workflow.entity;

import com.ale.venus.workflow.enumeration.FlowTaskState;
import com.ale.venus.workflow.model.node.FlowNode;
import com.ale.venus.workflow.support.IdGeneratorSupport;
import com.ale.venus.workflow.support.JsonVariableAccessor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 流程任务
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
public class FlowTask extends FlowEntity implements JsonVariableAccessor {

    /**
     * 任务名称
     */
    protected String name;

    /**
     * 流程实例ID
     */
    protected String instanceId;

    /**
     * 执行记录ID
     */
    protected String executionId;

    /**
     * 节点id
     * 对应设计信息中的节点ID
     */
    protected String nodeId;

    /**
     * 父级任务ID
     */
    protected String parentId;

    /**
     * 任务类型
     *
     * @see com.ale.venus.workflow.enumeration.FlowTaskType
     */
    protected String type;

    /**
     * 拥有人ID
     */
    protected String ownerId;

    /**
     * 受理人ID
     */
    protected String assigneeId;

    /**
     * 期望完成时间
     */
    protected LocalDateTime expireTime;

    /**
     * 提醒次数
     */
    protected Integer remindCount;

    /**
     * 是否已阅
     */
    protected Boolean viewed;

    /**
     * 任务变量
     */
    protected String variable;

    /**
     * 任务状态
     *
     * @see FlowTaskState
     */
    protected String state;

    /**
     * 创建任务
     *
     * @param instance  流程实例
     * @param flowNode  节点
     * @param variables 任务变量
     * @return 任务
     */
    public static FlowTask of(FlowInstance instance, FlowNode flowNode, Map<String, Object> variables) {
        FlowTask task = new FlowTask();
        task.setId(IdGeneratorSupport.generateId());
        task.setTenantId(instance.getTenantId());
        task.setNodeId(flowNode.getId());
        task.setType(flowNode.getType());
        task.setInstanceId(instance.getId());
        task.setName(flowNode.getName());
        task.addVariable(variables);
        task.setState(FlowTaskState.ACTIVE.getValue());
        task.setCreatedBy(instance.getCreatedBy());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedBy(instance.getCreatedBy());
        task.setUpdatedAt(LocalDateTime.now());
        task.setRemindCount(0);
        return task;
    }
}
