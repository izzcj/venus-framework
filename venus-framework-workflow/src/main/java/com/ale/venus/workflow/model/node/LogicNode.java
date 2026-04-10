package com.ale.venus.workflow.model.node;

import com.ale.venus.workflow.config.VenusFlowProperties;
import com.ale.venus.workflow.entity.FlowExecution;
import com.ale.venus.workflow.entity.FlowInstance;
import com.ale.venus.workflow.entity.FlowTask;
import com.ale.venus.workflow.support.FlowContext;

import java.util.Map;

/**
 * 逻辑节点
 * 无用户参与的节点
 *
 * @author Ale
 * @version 1.0.0
 */
public abstract class LogicNode extends FlowNode {

    @Override
    public boolean execute(FlowInstance instance, FlowExecution lastExecution, Map<String, Object> variable) {
        VenusFlowProperties properties = FlowContext.getProperties();
        if (properties.isGenerateLogicTask()) {
            FlowTask task = FlowTask.of(instance, this, null);
            task.setViewed(true);
            return this.doExecute(instance, lastExecution, variable, task);
        }
        return this.doExecute(instance, lastExecution, variable, null);
    }

    /**
     * 执行节点
     *
     * @param instance      流程实例
     * @param lastExecution 上次执行信息
     * @param variable      流程变量
     * @param task          流程任务
     * @return 是否执行成功
     */
    protected abstract boolean doExecute(FlowInstance instance, FlowExecution lastExecution, Map<String, Object> variable, FlowTask task);
}
