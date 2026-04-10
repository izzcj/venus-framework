package com.ale.venus.core.db;

import com.ale.venus.core.db.datasource.DataSourceConfigurer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.sql.DataSource;

/**
 * 数据库自动配置类
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
@AutoConfiguration
@RequiredArgsConstructor
@ComponentScan(basePackageClasses = ComponentScanMark.class)
@EnableConfigurationProperties(VenusDataBaseProperties.class)
public class DataBaseAutoConfiguration {

    /**
     * 数据库配置
     */
    private final VenusDataBaseProperties venusDataBaseProperties;

    /**
     * 数据源配置
     *
     * @param dataSourceConfigurers 数据源配置
     * @return Druid数据源
     */
    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource(ObjectProvider<DataSourceConfigurer> dataSourceConfigurers) {
        if (dataSourceConfigurers.stream().findAny().isEmpty()) {
            throw new RuntimeException("未找到任何数据源配置！");
        }
        VenusDataBaseProperties.Pool pool = this.venusDataBaseProperties.getPool();
        DataSource dataSource = dataSourceConfigurers.orderedStream()
            .filter(dataSourceConfigurer -> dataSourceConfigurer.supports(pool.getPoolType()))
            .findFirst()
            .map(dataSourceConfigurer -> dataSourceConfigurer.config(this.venusDataBaseProperties))
            .orElse(null);
        if (dataSource == null) {
            throw new RuntimeException("不支持的数据源：" + pool.getPoolType());
        }
        log.info("DataSource 初始化成功！当前使用连接池：{}", pool.getPoolType());
        return dataSource;
    }
}
