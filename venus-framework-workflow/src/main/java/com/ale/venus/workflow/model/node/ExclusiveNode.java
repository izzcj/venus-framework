package com.ale.venus.workflow.model.node;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import com.ale.venus.workflow.constants.FlowVariableConstants;
import com.ale.venus.workflow.entity.FlowExecution;
import com.ale.venus.workflow.entity.FlowInstance;
import com.ale.venus.workflow.entity.FlowTask;
import com.ale.venus.workflow.enumeration.FlowTaskType;
import com.ale.venus.workflow.exception.FlowException;
import com.ale.venus.workflow.model.InstanceModel;
import com.ale.venus.workflow.model.InstanceModelSupport;
import com.ale.venus.workflow.support.FlowContext;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 互斥分支节点
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExclusiveNode extends BranchNode {

    /**
     * 节点类型
     */
    public static final String NODE_TYPE = "exclusive";

    @Override
    public boolean doExecute(FlowInstance instance, FlowExecution lastExecution, Map<String, Object> variable, FlowTask task) {
        if (CollectionUtil.isEmpty(variable)) {
            variable = Maps.newHashMap();
            variable.put(FlowVariableConstants.PASS_BRANCH_COUNT, 1);
        }
        FlowExecution exclusiveExecution = FlowContext.getExecutionService().createExecution(instance, this.id, lastExecution, variable);
        if (task != null) {
            task.setExecutionId(exclusiveExecution.getId());
            task.setType(FlowTaskType.EXCLUSIVE.getValue());
            FlowContext.getTaskService().addTask(List.of(task), Collections.emptyList(), false);
        }

        boolean executeDefaultBranch = true;
        FlowNode defaultBranch = null;
        InstanceModel instanceModel = InstanceModelSupport.parseInstanceModel(instance);
        List<FlowNode> barchList = instanceModel.findBranch(this.getId());
        // 找到满足条件的分支执行
        for (FlowNode branch : barchList) {
            if (branch instanceof ConditionNode conditionNode) {
                if (BooleanUtil.isTrue(conditionNode.getDefaultCondition())) {
                    defaultBranch = conditionNode;
                    continue;
                }
                boolean executeResult = conditionNode.execute(instance, exclusiveExecution, null);
                // 互斥分支只有一条执行路径
                if (executeResult) {
                    executeDefaultBranch = false;
                    break;
                }
            } else {
                throw new FlowException("互斥分支的所有分支第一个节点必须为条件节点！");
            }
        }
        if (executeDefaultBranch && defaultBranch != null) {
            return defaultBranch.execute(instance, exclusiveExecution, null);
        }
        return true;
    }
}
