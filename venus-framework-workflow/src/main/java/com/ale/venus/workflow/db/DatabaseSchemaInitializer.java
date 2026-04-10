package com.ale.venus.workflow.db;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.ale.venus.workflow.config.VenusFlowProperties;
import com.ale.venus.workflow.exception.FlowException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库表初始化器
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class DatabaseSchemaInitializer implements InitializingBean {

    /**
     * 流程配置
     */
    private final VenusFlowProperties venusFlowProperties;

    /**
     * 数据源
     */
    private final DataSource dataSource;

    /**
     * SQL片段构建器
     */
    private final ObjectProvider<SqlStatementsBuilder> sqlStatementsBuilders;

    /**
     * SQL文件基路径
     */
    private static final String SQL_FILE_BASE_PATH = "/db";

    /**
     * 数据库类型映射
     */
    private static final Map<String, String> DATABASE_TYPE_MAP = new HashMap<>();

    static {
        DATABASE_TYPE_MAP.put("mysql", "mysql");
        DATABASE_TYPE_MAP.put("postgresql", "postgresql");
        DATABASE_TYPE_MAP.put("oracle", "oracle");
    }

    @Override
    public void afterPropertiesSet() {
        if (BooleanUtil.isTrue(this.venusFlowProperties.isGenerateDatabaseTable())) {
            try (Connection connection = dataSource.getConnection()) {
                String dbType = deduceDatabaseType(connection);
                String sqlFile = "/flow_engine_init_" + dbType + ".sql";

                executeSqlScript(connection, sqlFile, dbType);
                log.info("✅ 数据源[{}]流程引擎表加载完毕", dbType);
            } catch (Exception e) {
                throw new FlowException("初始化流程引擎表失败", e);
            }
        }
    }

    /**
     * 执行SQL脚本
     *
     * @param connection  数据库连接
     * @param dbType      数据库类型
     * @param sqlFilePath SQL文件路径
     */
    private void executeSqlScript(Connection connection, String sqlFilePath, String dbType) throws IOException, SQLException {
        Resource resource = new ClassPathResource(SQL_FILE_BASE_PATH + sqlFilePath);
        if (!resource.exists()) {
            throw new FileNotFoundException("找不到 SQL 文件: " + sqlFilePath);
        }

        String sql = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        try (Statement statement = connection.createStatement()) {
            for (SqlStatementsBuilder sqlStatementsBuilder : sqlStatementsBuilders) {
                if (sqlStatementsBuilder.supports(dbType)) {
                    List<String> sqlStatements = sqlStatementsBuilder.buildSqlStatements(sql);
                    for (String sqlStatement : sqlStatements) {
                        statement.execute(sqlStatement);
                    }
                    return;
                }
            }
            throw new UnsupportedOperationException(StrUtil.format("未找到[{}]数据库的sql片段构建器", dbType));
        }
    }

    /**
     * 推断数据库类型
     *
     * @param connection 数据库连接
     * @return 数据库类型
     */
    private String deduceDatabaseType(Connection connection) throws SQLException {
        String productName = connection.getMetaData().getDatabaseProductName().toLowerCase();
        String databaseType = DATABASE_TYPE_MAP.get(productName);
        if (StrUtil.isBlank(databaseType)) {
            throw new UnsupportedOperationException("不支持的数据库类型: " + productName);
        }
        return databaseType;
    }
}
