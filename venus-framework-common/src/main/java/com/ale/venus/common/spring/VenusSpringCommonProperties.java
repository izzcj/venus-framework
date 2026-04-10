package com.ale.venus.common.spring;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Venus WebMvc配置
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "venus.common")
public class VenusSpringCommonProperties {

    /**
     * 是否启用跨域
     */
    private boolean enableCors;

}
