package com.ale.venus.workflow.model.node;

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
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * 并行分支节点
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParallelNode extends BranchNode {

    /**
     * 节点类型
     */
    public static final String NODE_TYPE = "parallel";

    /**
     * 是否汇聚结果
     * <p>
     * 不汇聚：只要有分支完成就进入下一节点
     * 汇聚：则需要满足条件的分支均执行完成才会进入下一节点
     */
    @JsonProperty(index = 41)
    private boolean gatherResult;

    @Override
    public boolean doExecute(FlowInstance instance, FlowExecution lastExecution, Map<String, Object> variable, FlowTask task) {
        FlowExecution parallelExecution = FlowContext.getExecutionService().createExecution(instance, this.id, lastExecution, variable);
        if (task != null) {
            task.setExecutionId(parallelExecution.getId());
            task.setType(FlowTaskType.PARALLEL.getValue());
            FlowContext.getTaskService().addTask(List.of(task), Collections.emptyList(), false);
        }

        int passBranchCount = 0;
        InstanceModel instanceModel = InstanceModelSupport.parseInstanceModel(instance);
        List<FlowNode> branchList = instanceModel.findBranch(this.getId());

        // 如果用户选择了执行分支则执行用户选择的分支
        Object executeBranchIndex = lastExecution.getVariableByKey(FlowVariableConstants.EXECUTE_BRANCH_INDEX);
        if (executeBranchIndex != null) {
            if (executeBranchIndex instanceof Collection<?> indexes) {
                for (Object index : indexes) {
                    FlowNode flowNode = branchList.get(Integer.parseInt(index.toString()));
                    if (flowNode == null) {
                        throw new FlowException("执行选择的分支失败！分支[{}]不存在！", index);
                    }
                    if (flowNode.execute(instance, parallelExecution, null)) {
                        passBranchCount++;
                    }
                }
            }
        } else {
            for (FlowNode flowNode : branchList) {
                if (flowNode.execute(instance, parallelExecution, null)) {
                    passBranchCount++;
                }
            }
            if (!this.gatherResult) {
                passBranchCount = 1;
            }
        }
        if (passBranchCount == 0) {
            throw new FlowException("执行并行分支[{}]失败！没有任何分支可以执行！", this.name);
        }
        parallelExecution.addVariable(FlowVariableConstants.PASS_BRANCH_COUNT, passBranchCount);
        return FlowContext.getExecutionService().updateExecution(parallelExecution);
    }
}
