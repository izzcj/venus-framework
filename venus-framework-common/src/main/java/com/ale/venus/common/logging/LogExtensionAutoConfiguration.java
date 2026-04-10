package com.ale.venus.common.logging;

import ch.qos.logback.classic.Logger;
import cn.hutool.core.collection.CollectionUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Logback拓展自动配置
 *
 * @author Ale
 * @version 1.0.0
 */
@AutoConfiguration
@RequiredArgsConstructor
@ComponentScan(basePackageClasses = ComponentScanMark.class)
@EnableConfigurationProperties(VenusLoggingProperties.class)
public class LogExtensionAutoConfiguration {

    /**
     * Log配置
     */
    private final VenusLoggingProperties venusLoggingProperties;

    /**
     * Log请求信息MDC过滤器Bean
     *
     * @param securityProperties 安全配置
     * @return 过滤器Bean
     */
    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public LogRequestMdcFilter logbackWebMdcFilter(ObjectProvider<SecurityProperties> securityProperties) {
        return new LogRequestMdcFilter(this.getMdcFilterOrder(securityProperties));
    }

    /**
     * 获取MDC过滤器排序
     * 需要在Security配置之后，否则拿不到用户授权信息
     *
     * @param securityProperties 安全配置
     * @return 排序
     */
    private int getMdcFilterOrder(ObjectProvider<SecurityProperties> securityProperties) {
        return securityProperties.stream()
            .map(properties -> properties.getFilter().getOrder() + 10)
            .findFirst()
            .orElse(SecurityProperties.DEFAULT_FILTER_ORDER + 10);
    }

    /**
     * LoggerBean
     *
     * @param context       上下文
     * @param loggingSystem 日志系统
     * @return LoggerBean
     */
    @Bean
    @ConditionalOnProperty(prefix = "venus.logging", name = "enabled", havingValue = "true")
    public Logger logger(ApplicationContext context, LoggingSystem loggingSystem) {
        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        if (CollectionUtil.isNotEmpty(this.venusLoggingProperties.getLevel())) {
            this.venusLoggingProperties.getLevel().forEach(loggingSystem::setLogLevel);
        }
        if (CollectionUtil.isEmpty(this.venusLoggingProperties.getLogFile())) {
            VenusLoggingProperties.FileConfig defaultLogFileConfig = new VenusLoggingProperties.FileConfig();

            logger.addAppender(LogAppenderProvider.provideAppender(context, LogLevel.INFO, defaultLogFileConfig));

            logger.addAppender(LogAppenderProvider.provideAppender(context, LogLevel.ERROR, defaultLogFileConfig));
            return logger;
        }
        this.venusLoggingProperties.getLogFile().forEach((level, fileConfig) ->
            logger.addAppender(LogAppenderProvider.provideAppender(context, level, fileConfig))
        );
        return logger;
    }
}
