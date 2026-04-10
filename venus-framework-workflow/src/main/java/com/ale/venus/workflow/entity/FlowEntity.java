package com.ale.venus.workflow.entity;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 流程实体
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@FieldNameConstants
public abstract class FlowEntity implements Serializable {

    /**
     * 序列化版本号
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    protected String id;

    /**
     * 机构ID
     */
    protected String tenantId;

    /**
     * 创建时间
     */
    protected LocalDateTime createdAt;

    /**
     * 创建人
     */
    protected String createdBy;

    /**
     * 更新时间
     */
    protected LocalDateTime updatedAt;

    /**
     * 更新人
     */
    protected String updatedBy;
}
