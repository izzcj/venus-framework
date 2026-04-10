package com.ale.venus.core.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

/**
 * 审计查询条件基类
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@FieldNameConstants(asEnum = true)
@EqualsAndHashCode(callSuper = true)
public abstract class BaseAuditQuery extends BaseQuery {

    /**
     * 创建人
     */
    @Query(column = "create_by")
    private String createBy;

    /**
     * 更新人
     */
    @Query(column = "update_by")
    private String updateBy;
}
