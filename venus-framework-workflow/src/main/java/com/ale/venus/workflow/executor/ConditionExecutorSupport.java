package com.ale.venus.workflow.executor;

import cn.hutool.core.collection.CollectionUtil;
import com.ale.venus.workflow.model.Condition;
import com.ale.venus.workflow.model.ConditionGroup;
import com.ale.venus.workflow.parser.ConditionParser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 条件执行器支持
 *
 * @author Ale
 * @version 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConditionExecutorSupport {

    /**
     * 条件执行器
     */
    private static ConditionExecutor conditionExecutor;

    /**
     * 条件解析器
     */
    private static ConditionParser conditionParser;

    /**
     * 设置条件执行器
     *
     * @param conditionExecutor 条件执行器
     */
    static void setConditionExecutor(ConditionExecutor conditionExecutor) {
        ConditionExecutorSupport.conditionExecutor = conditionExecutor;
    }

    /**
     * 设置条件解析器
     *
     * @param conditionParser 条件解析器
     */
    static void setConditionParser(ConditionParser conditionParser) {
        ConditionExecutorSupport.conditionParser = conditionParser;
    }


    /**
     * 执行条件
     *
     * @param conditionGroups 条件组
     * @param args            参数
     * @return 执行结果
     */
    public static boolean executeCondition(List<ConditionGroup> conditionGroups, Map<String, Object> args) {
        if (CollectionUtil.isEmpty(conditionGroups)) {
            return false;
        }
        if (conditionExecutor == null) {
            throw new NullPointerException("未找到条件执行器");
        }
        for (ConditionGroup conditionGroup : conditionGroups) {
            for (Condition condition : conditionGroup.getConditions()) {
                conditionParser.parser(condition);
            }
        }
        return conditionExecutor.execute(conditionGroups, args);
    }
}
