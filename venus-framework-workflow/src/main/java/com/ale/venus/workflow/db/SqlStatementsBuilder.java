package com.ale.venus.workflow.db;

import com.ale.venus.common.support.Supportable;

import java.util.List;

/**
 * sql片段构建器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface SqlStatementsBuilder extends Supportable<String> {

    /**
     * 构建sql片段
     *
     * @param sql    sql
     * @return sql片段
     */
    List<String> buildSqlStatements(String sql);

}
