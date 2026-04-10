package com.ale.venus.workflow.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 流程变量常量
 *
 * @author Ale
 * @version 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FlowVariableConstants {

    /**
     * 任务受理人集合
     */
    public static final String TASK_ASSIGNEES = "taskAssignees";

    /**
     * 累计权重
     */
    public static final String ACCUMULATIVE_WEIGHT = "accumulativeWeight";

    /**
     * 完成分支数
     */
    public static final String COMPLETE_BRANCH_COUNT = "completeBranchCount";

    /**
     * 通过分支数
     */
    public static final String PASS_BRANCH_COUNT = "passBranchCount";

    /**
     * 执行分支索引
     */
    public static final String EXECUTE_BRANCH_INDEX = "executeBranchIndex";

    /**
     * 跳转执行ID
     */
    public static final String JUMP_EXECUTION_ID = "jumpExecutionId";

}
