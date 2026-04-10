package com.ale.venus.workflow.model.node;

import cn.hutool.core.collection.CollectionUtil;
import com.ale.venus.workflow.entity.FlowExecution;
import com.ale.venus.workflow.entity.FlowInstance;
import com.ale.venus.workflow.entity.FlowTask;
import com.ale.venus.workflow.entity.FlowTaskActor;
import com.ale.venus.workflow.model.TaskAssignee;
import com.ale.venus.workflow.support.FlowContext;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.function.Function;


/**
 * 抄送节点
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CarbonCopyNode extends UserNode {

    /**
     * 节点类型
     */
    public static final String NODE_TYPE = "carbonCopy";

    @Override
    protected Function<FlowTask, Boolean> buildTaskFunction() {
        return task -> false;
    }

    @Override
    protected boolean doExecute(FlowInstance instance, FlowExecution execution, List<TaskAssignee> taskAssignees, List<FlowTask> tasks, List<FlowTaskActor> taskActors) {
        if (CollectionUtil.isNotEmpty(tasks)) {
            FlowContext.getTaskService().addTask(tasks, taskActors, true);
            FlowContext.getTaskService().forceFinishTask(execution.getId(), null);
        }
        return FlowContext.getExecutionService().completeExecution(execution);
    }
}
