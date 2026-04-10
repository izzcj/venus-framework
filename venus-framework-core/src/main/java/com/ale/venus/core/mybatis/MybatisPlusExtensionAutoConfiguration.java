package com.ale.venus.core.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.AutoMappingUnknownColumnBehavior;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Mybatis-plus拓展自动配置
 *
 * @author Ale
 * @version 1.0.0
 */
@AutoConfiguration
@AutoConfigureBefore(MybatisPlusAutoConfiguration.class)
public class MybatisPlusExtensionAutoConfiguration {

    /**
     * 分页插件
     *
     * @param dataSource 数据源
     * @return MybatisPlusInterceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(DataSource dataSource) throws SQLException {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        DbType dbType = DbType.getDbType(
            dataSource.getConnection()
                .getMetaData()
                .getDatabaseProductName()
                .toLowerCase()
        );
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(dbType));
        return mybatisPlusInterceptor;
    }

    /**
     * 拓展MetaObjectHandler
     *
     * @return MetaObjectHandler
     */
    @Bean
    @ConditionalOnMissingBean
    public MetaObjectHandler metaObjectHandler() {
        return new AuditMetaObjectHandler();
    }

    /**
     * Mybatis-plus配置
     *
     * @return 配置Bean
     */
    @Bean
    public ConfigurationCustomizer mybatisPlusConfigurationCustomizer() {
        return configuration -> {
            configuration.setMapUnderscoreToCamelCase(true);
            configuration.setAutoMappingBehavior(AutoMappingBehavior.FULL);
            configuration.setAutoMappingUnknownColumnBehavior(AutoMappingUnknownColumnBehavior.WARNING);
            configuration.setLocalCacheScope(LocalCacheScope.SESSION);
            configuration.setCacheEnabled(true);
            configuration.setCallSettersOnNulls(false);
            configuration.setDefaultEnumTypeHandler(EnumTypeHandler.class);
            configuration.setDefaultExecutorType(ExecutorType.REUSE);
        };
    }
}
