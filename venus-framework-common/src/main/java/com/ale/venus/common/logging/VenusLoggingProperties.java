package com.ale.venus.common.logging;

import com.ale.venus.common.support.EnableAwareProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.logging.LogLevel;

import java.util.Map;

/**
 * Venus日志配置
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "venus.logging")
public class VenusLoggingProperties extends EnableAwareProperties {

    /**
     * 日志级别
     * 项目地址 + 日志级别
     */
    private Map<String, LogLevel> level;

    /**
     * 日志文件配置
     */
    private Map<LogLevel, FileConfig> logFile;

    /**
     * 文件配置
     */
    @Data
    public static class FileConfig {

        /**
         * 日志文件路径
         * 默认为 logs/{ApplicationName}/{LogLevel}.log
         */
        private String filePath;

        /**
         * 单个日志文件最大大小，单位MB
         */
        private int maxFileSize = 30;

        /**
         * 日志文件保留时长，单位天
         */
        private int maxHistory = 30;

        /**
         * 日志文件大小上限
         * 超过该值将删除日志文件
         */
        private long totalSizeCap = 500;

        /**
         * 日志文件大小单位
         */
        private String sizeUnit = "MB";

        /**
         * 是否异步写入日志文件
         */
        private boolean async = true;
    }
}
