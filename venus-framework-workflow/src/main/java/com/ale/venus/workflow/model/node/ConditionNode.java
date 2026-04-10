package com.ale.venus.workflow.model.node;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import com.ale.venus.workflow.entity.FlowExecution;
import com.ale.venus.workflow.entity.FlowInstance;
import com.ale.venus.workflow.entity.FlowTask;
import com.ale.venus.workflow.enumeration.FlowTaskType;
import com.ale.venus.workflow.executor.ConditionExecutorSupport;
import com.ale.venus.workflow.model.ConditionGroup;
import com.ale.venus.workflow.support.FlowContext;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 条件节点
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConditionNode extends LogicNode {

    /**
     * 节点类型
     */
    public static final String NODE_TYPE = "condition";

    /**
     * 是否为默认条件
     */
    @JsonProperty(index = 31)
    private Boolean defaultCondition;

    /**
     * 条件组
     */
    @JsonProperty(index = 32)
    private List<ConditionGroup> conditionGroup;


    @Override
    public boolean doExecute(FlowInstance instance, FlowExecution lastExecution, Map<String, Object> variable, FlowTask task) {
        Map<String, Object> globalVariable = instance.getAllVariable();
        if (CollectionUtil.isNotEmpty(variable)) {
            globalVariable.putAll(variable);
        }
        // 满足条件才执行
        if (BooleanUtil.isTrue(this.defaultCondition) || ConditionExecutorSupport.executeCondition(this.conditionGroup, globalVariable)) {
            FlowExecution conditionExecution = FlowContext.getExecutionService().createExecution(instance, this.id, lastExecution, null);
            if (task != null) {
                task.setExecutionId(conditionExecution.getId());
                task.setType(FlowTaskType.CONDITION.getValue());
                FlowContext.getTaskService().addTask(List.of(task), Collections.emptyList(), false);
            }
            return FlowContext.getExecutionService().completeExecution(conditionExecution);
        }
        return false;
    }
}
