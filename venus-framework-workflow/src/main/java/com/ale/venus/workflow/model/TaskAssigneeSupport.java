package com.ale.venus.workflow.model;

import cn.hutool.core.collection.CollectionUtil;
import com.ale.venus.workflow.parser.TaskAssigneeParser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 任务受理人支持
 *
 * @author Ale
 * @version 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TaskAssigneeSupport {

    /**
     * 任务受理人解析器
     */
    private static TaskAssigneeParser taskAssigneeParser;

    /**
     * 设置任务受理人解析器
     *
     * @param taskAssigneeParser 任务受理人解析器
     */
    static void setAssigneeParser(TaskAssigneeParser taskAssigneeParser) {
        TaskAssigneeSupport.taskAssigneeParser = taskAssigneeParser;
    }

    /**
     * 解析任务受理人
     *
     * @param assigneeConfig 任务受理人配置
     * @return 任务受理人列表
     */
    public static List<TaskAssignee> parse(List<AssigneeConfig> assigneeConfig) {
        if (CollectionUtil.isEmpty(assigneeConfig)) {
            return Collections.emptyList();
        }
        List<TaskAssignee> result = taskAssigneeParser.parser(assigneeConfig);
        if (CollectionUtil.isEmpty(result)) {
            return Collections.emptyList();
        }
        return result;
    }
}
