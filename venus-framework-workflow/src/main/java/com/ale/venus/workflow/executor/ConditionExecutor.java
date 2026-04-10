package com.ale.venus.workflow.executor;

import com.ale.venus.workflow.model.ConditionGroup;

import java.util.List;
import java.util.Map;

/**
 * 条件执行器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface ConditionExecutor {

    /**
     * 执行条件
     *
     * @param conditionGroups 条件组
     * @param args            参数
     * @return 执行结果
     */
    boolean execute(List<ConditionGroup> conditionGroups, Map<String, Object> args);

}
