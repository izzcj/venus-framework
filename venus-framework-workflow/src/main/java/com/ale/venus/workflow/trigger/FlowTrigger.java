package com.ale.venus.workflow.trigger;

import com.ale.venus.workflow.entity.FlowInstance;

import java.util.Map;

/**
 * 流程触发器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface FlowTrigger {

    /**
     * 执行流程触发器
     *
     * @param instance  流程实例
     * @param param     参数
     * @return 是否执行成功
     */
    boolean execute(FlowInstance instance, Map<String, Object> param);

}
