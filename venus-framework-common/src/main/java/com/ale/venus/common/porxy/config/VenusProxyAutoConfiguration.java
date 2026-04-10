package com.ale.venus.common.porxy.config;

import cn.hutool.core.util.BooleanUtil;
import com.ale.venus.common.porxy.ComponentScanMark;
import com.ale.venus.common.porxy.ProxyFactory;
import com.ale.venus.common.porxy.cglib.CglibProxyFactory;
import com.ale.venus.common.porxy.jdk.JDKProxyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.ProxyConfig;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Venus代理自动配置
 *
 * @author Ale
 * @version 1.0.0
 */
@AutoConfiguration
@RequiredArgsConstructor
@ComponentScan(basePackageClasses = ComponentScanMark.class)
@EnableConfigurationProperties(VenusProxyProperties.class)
public class VenusProxyAutoConfiguration {

    /**
     * 代理工厂
     *
     * @param context Spring上下文
     * @return Venus代理Bean
     */
    @Bean
    @ConditionalOnProperty(prefix = "venus.proxy", name = "enabled", havingValue = "true")
    public ProxyFactory proxyFactory(ConfigurableApplicationContext context) {
        ProxyConfig apc = context.getBean(AbstractAutoProxyCreator.class);
        boolean proxyTargetClass = apc.isProxyTargetClass();
        if (BooleanUtil.isTrue(proxyTargetClass)) {
            return new CglibProxyFactory();
        } else {
            return new JDKProxyFactory();
        }
    }
}
