package com.ale.venus.workflow.support;

import com.ale.venus.workflow.enumeration.VariableLevel;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 结束任务参数
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@Builder
public class FinishTaskParam {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 受理人ID
     */
    private String assigneeId;

    /**
     * 任务变量
     */
    private Map<String, Object> variable;

    /**
     * 变量级别
     */
    private VariableLevel variableLevel;

    /**
     * 评论
     */
    private String comment;

    /**
     * 附件
     */
    private List<String> attachments;

}
