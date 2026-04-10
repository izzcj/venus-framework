package com.ale.venus.core.db.datasource;

import com.ale.venus.common.support.Supportable;
import com.ale.venus.core.db.VenusDataBaseProperties;
import org.springframework.core.Ordered;

import javax.sql.DataSource;

/**
 * 数据源配置器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface DataSourceConfigurer extends Supportable<VenusDataBaseProperties.PoolType>, Ordered {

    /**
     * 配置数据源
     *
     * @param venusDataBaseProperties 数据库配置
     * @return {@link DataSource}
     */
    DataSource config(VenusDataBaseProperties venusDataBaseProperties);
}
