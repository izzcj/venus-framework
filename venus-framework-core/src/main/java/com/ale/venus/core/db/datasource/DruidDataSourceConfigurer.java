package com.ale.venus.core.db.datasource;

import com.ale.venus.core.db.DatabaseType;
import com.ale.venus.core.db.VenusDataBaseProperties;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * Druid数据源配置器
 *
 * @author Ale
 * @version 1.0.0
 */
@Component
public class DruidDataSourceConfigurer implements DataSourceConfigurer {

    @Override
    public DataSource config(VenusDataBaseProperties venusDataBaseProperties) {
        DatabaseType databaseType = venusDataBaseProperties.getType();
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(databaseType.resolveUrl(venusDataBaseProperties));
        druidDataSource.setDriverClassName(databaseType.getDriverClassName());
        druidDataSource.setUsername(venusDataBaseProperties.getUsername());
        druidDataSource.setPassword(venusDataBaseProperties.getPassword());
        druidDataSource.setInitialSize(30);
        druidDataSource.setMinIdle(10);
        druidDataSource.setMaxActive(100);
        druidDataSource.setMaxWait(60000);
        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
        druidDataSource.setMinEvictableIdleTimeMillis(300000);
        druidDataSource.setValidationQuery("SELECT 1");
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);
        return druidDataSource;
    }

    @Override
    public boolean supports(VenusDataBaseProperties.PoolType poolType) {
        return Objects.equals(VenusDataBaseProperties.PoolType.Druid, poolType);
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
