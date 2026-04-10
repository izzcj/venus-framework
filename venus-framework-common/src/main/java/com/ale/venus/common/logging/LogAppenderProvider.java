package com.ale.venus.common.logging;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.util.FileSize;
import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.ApplicationContext;

/**
 * 日志输出配置提供器
 *
 * @author Ale
 * @version 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogAppenderProvider {

    /**
     * 提供日志输出器
     *
     * @param context    上下文
     * @param logLevel   日志级别
     * @param fileConfig 文件配置
     * @return 日志输出器
     */
    public static Appender<ILoggingEvent> provideAppender(ApplicationContext context, LogLevel logLevel, VenusLoggingProperties.FileConfig fileConfig) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        String appenderName;
        String filePathSuffix;

        if (logLevel == LogLevel.INFO) {
            appenderName = "info-log";
            filePathSuffix = "/info.log";
        } else {
            appenderName = "error-log";
            filePathSuffix = "/error.log";
        }

        // 基本配置
        RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<>();

        fileAppender.setContext(loggerContext);
        fileAppender.setName(appenderName);

        String filePath = fileConfig.getFilePath();
        if (StrUtil.isBlank(filePath)) {
            String applicationName = context.getEnvironment().getProperty("spring.application.name", String.class, "");
            filePath = "logs/" + applicationName + filePathSuffix;
        }

        fileAppender.setFile(filePath);

        // 日志级别配置
        fileAppender.addFilter(buildLevelFilter(logLevel));

        // 编码器配置
        fileAppender.setEncoder(buildLayoutWrappingEncoder(loggerContext));

        // 滚动策略配置
        fileAppender.setRollingPolicy(buildRollingPolicy(loggerContext, fileAppender, filePath, fileConfig));

        fileAppender.start();

        if (fileConfig.isAsync()) {
            return buildAsyncAppender(fileAppender, loggerContext);
        }
        return fileAppender;
    }

    /**
     * 构建日志级别过滤器
     *
     * @param level 日志级别
     * @return 日志级别过滤器
     */
    private static LevelFilter buildLevelFilter(LogLevel level) {
        LevelFilter levelFilter = new LevelFilter();
        levelFilter.setLevel(level == LogLevel.INFO ? Level.INFO : Level.ERROR);
        levelFilter.setOnMatch(FilterReply.ACCEPT);
        levelFilter.setOnMismatch(FilterReply.DENY);
        levelFilter.start();
        return levelFilter;
    }

    /**
     * 构建滚动策略
     *
     * @param context    日志上下文
     * @param appender   日志输出器
     * @param filePath   文件地址
     * @param fileConfig 日志文件配置
     * @return 滚动策略
     */
    private static RollingPolicy buildRollingPolicy(LoggerContext context, FileAppender<ILoggingEvent> appender, String filePath, VenusLoggingProperties.FileConfig fileConfig) {
        SizeAndTimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
        rollingPolicy.setContext(context);
        rollingPolicy.setParent(appender);
        rollingPolicy.setMaxHistory(fileConfig.getMaxHistory());
        rollingPolicy.setFileNamePattern(
            filePath.substring(0, filePath.lastIndexOf(".")) + ".%d{yyyy-MM-dd}.%i.log"
        );
        rollingPolicy.setMaxFileSize(
            FileSize.valueOf(fileConfig.getMaxFileSize() + fileConfig.getSizeUnit())
        );
        rollingPolicy.setTotalSizeCap(
            FileSize.valueOf(fileConfig.getTotalSizeCap() + fileConfig.getSizeUnit())
        );
        rollingPolicy.start();
        return rollingPolicy;
    }

    /**
     * 构建日志布局包装编码器
     *
     * @param loggerContext 日志上下文
     * @return 日志布局包装编码器
     */
    private static LayoutWrappingEncoder<ILoggingEvent> buildLayoutWrappingEncoder(LoggerContext loggerContext) {
        PatternLayout layout = new PatternLayout();
        // \x1B\[[\d;]*m 去掉ASCII中的颜色
        layout.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} - [%thread] %-5level %logger - %replace(%msg){\"\\x1B\\[[\\d;]*m\",\"\"}%n");
        layout.setContext(loggerContext);
        layout.start();
        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
        encoder.setLayout(layout);
        encoder.start();
        return encoder;
    }

    /**
     * 构建异步日志输出器
     *
     * @param appender      日志输出器
     * @param loggerContext 日志输出器
     * @return 异步输出器
     */
    private static AsyncAppender buildAsyncAppender(Appender<ILoggingEvent> appender, LoggerContext loggerContext) {
        AsyncAppender asyncAppender = new AsyncAppender();
        asyncAppender.setContext(loggerContext);
        asyncAppender.addAppender(appender);
        asyncAppender.setName("async-" + appender.getName());
        asyncAppender.setDiscardingThreshold(0);
        asyncAppender.setQueueSize(500);
        asyncAppender.start();
        return asyncAppender;
    }

}
