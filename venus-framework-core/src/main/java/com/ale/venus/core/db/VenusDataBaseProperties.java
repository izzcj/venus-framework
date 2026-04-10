package com.ale.venus.core.db;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Venus数据库配置
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "venus.db")
public class VenusDataBaseProperties {

    /**
     * 数据库类型
     */
    private DatabaseType type = DatabaseType.POSTGRESQL;

    /**
     * 数据库连接地址
     */
    private String url;

    /**
     * 数据库驱动
     */
    private String host;

    /**
     * 数据库端口
     */
    private Integer port;

    /**
     * 数据库名称
     */
    private String database;

    /**
     * 数据库模式
     */
    private String schema;

    /**
     * 数据库用户名
     */
    private String username;

    /**
     * 数据库密码
     */
    private String password;

    /**
     * 连接池配置
     */
    private Pool pool;

    /**
     * 连接池配置
     */
    @Data
    public static class Pool {

        /**
         * 连接池类型
         */
        private PoolType poolType = PoolType.Hikari;

        /**
         * 是否开启SQL监控
         */
        private boolean enableStat = true;
    }

    /**
     * 连接池类型
     */
    public enum PoolType {
        /**
         * Druid连接池
         */
        Druid,

        /**
         * Hikari连接池
         */
        Hikari
    }
}
