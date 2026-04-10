package com.ale.venus.workflow.support;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 启动流程实例参数
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@Builder
public class StartInstanceParam {

    /**
     * 流程定义ID
     */
    private String definitionId;

    /**
     * 业务id
     */
    private String businessId;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 流程标题
     */
    private String title;

    /**
     * 流程描述
     */
    private String description;

    /**
     * 流程变量
     */
    private Map<String, Object> variable;

    /**
     * 发起人ID
     */
    private String starterId;
}
