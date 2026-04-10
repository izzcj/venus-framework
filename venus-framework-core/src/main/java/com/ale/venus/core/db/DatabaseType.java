package com.ale.venus.core.db;

import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.enumeration.BaseEnum;
import com.baomidou.mybatisplus.annotation.DbType;
import lombok.Getter;

/**
 * 数据库类型
 *
 * @author Ale
 * @version 1.0.0
 */
@Getter
public enum DatabaseType implements BaseEnum<DbType> {

    /**
     * mysql
     */
    MYSQL(DbType.MYSQL, "com.mysql.cj.jdbc.Driver", "jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true") {
        @Override
        public String buildUrl(VenusDataBaseProperties venusDataBaseProperties) {
            return getUrlTemplate().formatted(
                venusDataBaseProperties.getHost(),
                venusDataBaseProperties.getPort(),
                venusDataBaseProperties.getDatabase()
            );
        }

        @Override
        public int provideDefaultPort() {
            return 3306;
        }
    },

    /**
     * postgresql
     */
    POSTGRESQL(DbType.POSTGRE_SQL, "org.postgresql.Driver", "jdbc:postgresql://%s:%d/%s?options=-c%%20search_path=%s&stringtype=unspecified&tcpKeepAlive=true") {
        @Override
        public String buildUrl(VenusDataBaseProperties venusDataBaseProperties) {
            return getUrlTemplate().formatted(
                venusDataBaseProperties.getHost(),
                venusDataBaseProperties.getPort(),
                venusDataBaseProperties.getDatabase(),
                venusDataBaseProperties.getSchema()
            );
        }

        @Override
        public int provideDefaultPort() {
            return 5432;
        }
    },

    /**
     * ORACLE
     */
    ORACLE(DbType.ORACLE, "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@%s:%d:%s") {
        @Override
        public String buildUrl(VenusDataBaseProperties venusDataBaseProperties) {
            return getUrlTemplate().formatted(
                venusDataBaseProperties.getHost(),
                venusDataBaseProperties.getPort(),
                venusDataBaseProperties.getDatabase()
            );
        }

        @Override
        public int provideDefaultPort() {
            return 1521;
        }
    },

    /**
     * SQL_SERVER
     */
    SQL_SERVER(DbType.SQL_SERVER, "com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://%s:%d;DatabaseName=%s") {
        @Override
        public String buildUrl(VenusDataBaseProperties venusDataBaseProperties) {
            return getUrlTemplate().formatted(
                venusDataBaseProperties.getHost(),
                venusDataBaseProperties.getPort(),
                venusDataBaseProperties.getDatabase()
            );
        }

        @Override
        public int provideDefaultPort() {
            return 1433;
        }
    };

    /**
     * 数据库类型
     */
    private final DbType value;

    /**
     * 数据库驱动类名
     */
    private final String driverClassName;

    /**
     * 数据库连接地址模板
     */
    private final String urlTemplate;

    DatabaseType(DbType value, String driverClassName, String urlTemplate) {
        this.value = value;
        this.driverClassName = driverClassName;
        this.urlTemplate = urlTemplate;
        init(value);
    }

    /**
     * 构建数据库连接地址
     *
     * @param venusDataBaseProperties 数据库配置
     * @return 数据库连接地址
     */
    public abstract String buildUrl(VenusDataBaseProperties venusDataBaseProperties);

    /**
     * 默认端口
     *
     * @return 默认端口
     */
    protected abstract int provideDefaultPort();

    /**
     * 解析数据库连接地址
     *
     * @param venusDataBaseProperties 数据库配置
     * @return 数据库连接地址
     */
    public String resolveUrl(VenusDataBaseProperties venusDataBaseProperties) {
        if (StrUtil.isNotBlank(venusDataBaseProperties.getUrl())) {
            return venusDataBaseProperties.getUrl();
        }
        if (StrUtil.isBlank(venusDataBaseProperties.getHost())) {
            venusDataBaseProperties.setHost("localhost");
        }
        if (venusDataBaseProperties.getPort() == null) {
            venusDataBaseProperties.setPort(provideDefaultPort());
        }
        return buildUrl(venusDataBaseProperties);
    }
}
