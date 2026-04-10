package com.ale.venus.workflow.entity;

import com.ale.venus.workflow.model.TaskAssignee;
import com.ale.venus.workflow.support.IdGeneratorSupport;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 流程任务参与人
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
public class FlowTaskActor implements Serializable {

    /**
     * ID
     */
    protected String id;

    /**
     * 流程实例ID
     */
    protected String instanceId;

    /**
     * 执行任务ID
     */
    protected String executionId;

    /**
     * 任务ID
     */
    protected String taskId;

    /**
     * 参与者ID
     */
    protected String actorId;

    /**
     * 受理人ID
     */
    protected String assigneeId;

    /**
     * 受理人类型
     *
     * @see com.ale.venus.workflow.enumeration.AssigneeType
     */
    protected String assigneeType;

    /**
     * 权重
     */
    protected Integer weight;

    /**
     * 扩展json
     */
    protected String extend;

    /**
     * 创建时间
     */
    protected LocalDateTime createdAt;

    /**
     * 创建任务参与人
     *
     * @param task         流程任务
     * @param taskAssignee 任务受理人
     * @return 任务参与人
     */
    public static FlowTaskActor of(FlowTask task, TaskAssignee taskAssignee) {
        FlowTaskActor taskActor = new FlowTaskActor();
        taskActor.setId(IdGeneratorSupport.generateId());
        taskActor.setCreatedAt(LocalDateTime.now());
        taskActor.setExecutionId(task.getExecutionId());
        taskActor.setInstanceId(task.getInstanceId());
        taskActor.setTaskId(task.getId());
        taskActor.setActorId(taskAssignee.getId());
        taskActor.setWeight(taskAssignee.getWeight());
        return taskActor;
    }
}
