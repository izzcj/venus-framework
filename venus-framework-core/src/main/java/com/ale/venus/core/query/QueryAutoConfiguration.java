package com.ale.venus.core.query;

import com.ale.venus.core.query.condition.ComponentScanMark;
import com.ale.venus.core.query.condition.QueryCondition;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * 查询自动配置
 *
 * @author Ale
 * @version 1.0.0
 */
@AutoConfiguration
@ComponentScan(basePackageClasses = ComponentScanMark.class)
public class QueryAutoConfiguration {

    /**
     * 查询条件解析器
     *
     * @param queryConditions 查询条件
     * @return 查询条件解析器
     */
    @Bean(autowireCandidate = false)
    public QueryConditionResolver queryConditionResolver(ObjectProvider<QueryCondition> queryConditions) {
        // noinspection InstantiationOfUtilityClass
        return new QueryConditionResolver(queryConditions);
    }

}
