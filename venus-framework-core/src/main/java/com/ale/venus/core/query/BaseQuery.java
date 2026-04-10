package com.ale.venus.core.query;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 查询条件基类
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
public abstract class BaseQuery {

    /**
     * id
     */
    @Query
    private Long id;

    /**
     * id集合
     */
    @Query(column = "id", type = QueryType.IN)
    private List<Long> ids;

    /**
     * 创建时间开始
     */
    @Query(column = "create_at", type = QueryType.GE)
    private LocalDateTime createAtBegin;

    /**
     * 创建时间结束
     */
    @Query(column = "create_at", type = QueryType.LE)
    private LocalDateTime createAtEnd;

    /**
     * 更新时间开始
     */
    @Query(column = "update_at", type = QueryType.GE)
    private LocalDateTime updateAtBegin;

    /**
     * 更新时间结束
     */
    @Query(column = "update_at", type = QueryType.LE)
    private LocalDateTime updateAtEnd;
}
