package com.ale.venus.workflow.model.node;

import com.ale.venus.workflow.entity.FlowExecution;
import com.ale.venus.workflow.entity.FlowInstance;

import java.util.Map;

/**
 * 可执行的流程节点
 *
 * @author Ale
 * @version 1.0.0
 */
public interface ExecutableNode {

    /**
     * 执行
     *
     * @param flowInstance  流程实例
     * @param lastExecution 上次执行
     * @param variable      变量
     * @return 执行结果
     */
    boolean execute(FlowInstance flowInstance, FlowExecution lastExecution, Map<String, Object> variable);

}
