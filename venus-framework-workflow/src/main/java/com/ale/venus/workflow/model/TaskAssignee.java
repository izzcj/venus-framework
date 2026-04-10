package com.ale.venus.workflow.model;

import lombok.Builder;
import lombok.Data;


/**
 * 任务受理人
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@Builder
public class TaskAssignee {

    /**
     * 受理人id
     */
    private String id;

    /**
     * 权重
     */
    private Integer weight;
}
