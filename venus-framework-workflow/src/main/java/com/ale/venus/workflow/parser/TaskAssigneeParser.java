package com.ale.venus.workflow.parser;

import com.ale.venus.workflow.model.AssigneeConfig;
import com.ale.venus.workflow.model.TaskAssignee;

import java.util.List;

/**
 * 任务受理人解析器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface TaskAssigneeParser {

    /**
     * 解析任务受理人
     *
     * @param assigneeConfig 任务受理人配置
     * @return 任务受理人
     */
    List<TaskAssignee> parser(List<AssigneeConfig> assigneeConfig);

}
