package com.ale.venus.common.porxy.config;

import com.ale.venus.common.support.EnableAwareProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Venus代理配置
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "venus.proxy")
public class VenusProxyProperties extends EnableAwareProperties {

}
