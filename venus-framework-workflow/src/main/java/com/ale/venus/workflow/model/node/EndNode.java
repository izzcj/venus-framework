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
 * 结束节点
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EndNode extends LogicNode {

    /**
     * 结束节点类型
     */
    public static final String NODE_TYPE = "end";

    @Override
    public boolean doExecute(FlowInstance instance, FlowExecution lastExecution, Map<String, Object> variable, FlowTask task) {
        FlowExecution endExecution = FlowContext.getExecutionService().createExecution(instance, this.id, lastExecution, variable);
        if (task != null) {
            task.setExecutionId(endExecution.getId());
            task.setType(FlowTaskType.END.getValue());
            FlowContext.getTaskService().addTask(List.of(task), Collections.emptyList(), false);
        }
        return FlowContext.getExecutionService().completeExecution(endExecution);
    }
}
