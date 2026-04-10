package com.ale.venus.core.db.datasource;

import com.ale.venus.core.db.DatabaseType;
import com.ale.venus.core.db.VenusDataBaseProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * Hikari数据源配置器
 *
 * @author Ale
 * @version 1.0.0
 */
@Component
public class HikariDataSourceConfigurer implements DataSourceConfigurer {

    @Override
    public DataSource config(VenusDataBaseProperties venusDataBaseProperties) {
        DatabaseType databaseType = venusDataBaseProperties.getType();
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(databaseType.resolveUrl(venusDataBaseProperties));
        hikariConfig.setDriverClassName(databaseType.getDriverClassName());
        hikariConfig.setUsername(venusDataBaseProperties.getUsername());
        hikariConfig.setPassword(venusDataBaseProperties.getPassword());
        hikariConfig.setAutoCommit(true);
        hikariConfig.setMinimumIdle(10);
        hikariConfig.setMaximumPoolSize(100);
        hikariConfig.setConnectionTimeout(10000);
        hikariConfig.setKeepaliveTime(1);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setIdleTimeout(300000);
        hikariConfig.setAllowPoolSuspension(false);
        return new HikariDataSource(hikariConfig);
    }

    @Override
    public boolean supports(VenusDataBaseProperties.PoolType poolType) {
        return Objects.equals(VenusDataBaseProperties.PoolType.Hikari, poolType);
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
