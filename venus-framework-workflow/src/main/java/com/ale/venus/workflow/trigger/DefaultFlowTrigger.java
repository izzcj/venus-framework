package com.ale.venus.workflow.trigger;

import com.ale.venus.common.support.Comment;
import com.ale.venus.workflow.entity.FlowInstance;

import java.util.Map;

/**
 * 默认流程触发器
 *
 * @author Ale
 * @version 1.0.0
 */
@Comment("默认触发器")
public class DefaultFlowTrigger implements FlowTrigger {

    @Override
    public boolean execute(FlowInstance instance, Map<String, Object> param) {
        // 不做任何处理
        return true;
    }
}
