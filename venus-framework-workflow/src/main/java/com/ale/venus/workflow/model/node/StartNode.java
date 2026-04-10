package com.ale.venus.workflow.model.node;

import com.ale.venus.workflow.entity.FlowExecution;
import com.ale.venus.workflow.entity.FlowInstance;
import com.ale.venus.workflow.entity.FlowTask;
import com.ale.venus.workflow.enumeration.FlowTaskType;
import com.ale.venus.workflow.support.FlowContext;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 开始节点
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StartNode extends LogicNode {

    /**
     * 节点类型
     */
    public static final String NODE_TYPE = "start";

    @Override
    public boolean doExecute(FlowInstance instance, FlowExecution lastExecution, Map<String, Object> variable, FlowTask task) {
        FlowExecution startExecution = FlowContext.getExecutionService().createExecution(instance, this.id, lastExecution, variable);
        if (task != null) {
            task.setExecutionId(startExecution.getId());
            task.setType(FlowTaskType.START.getValue());
            task.setAssigneeId(instance.getCreatedBy());
            FlowContext.getTaskService().addTask(List.of(task), Collections.emptyList(), false);
        }
        return FlowContext.getExecutionService().completeExecution(startExecution);
    }
}
