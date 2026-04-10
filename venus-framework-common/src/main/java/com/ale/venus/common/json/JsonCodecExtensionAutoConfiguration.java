package com.ale.venus.common.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Locale;

/**
 * JSON序列化反序列化编码拓展自动配置
 *
 * @author Ale
 * @version 1.0.0
 */
@AutoConfiguration
@ComponentScan(basePackageClasses = ComponentScanMark.class)
public class JsonCodecExtensionAutoConfiguration {

    /**
     * 配置Jackson
     *
     * @return Jackson构建器自定义器Bean
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer venusJackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder
            .autoDetectGettersSetters(false)
            // 自动检测field
            .autoDetectFields(true)
            // 默认日期格式
            .simpleDateFormat("yyyy-MM-dd HH:mm:ss")
            // 区域
            .locale(Locale.CHINA)
            // 包含所有字段
            .serializationInclusion(JsonInclude.Include.ALWAYS)
            // 允许对任何字段进行访问
            .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            // 存在未知属性时，不抛出异常
            .failOnUnknownProperties(false)
            // 允许对任何字符进行反斜杠转义
            .featuresToEnable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature())
            // 将自引用写为 NULL
            .featuresToEnable(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL)
            // 将BIG_DECIMAL以纯文本形式写入
            .featuresToEnable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
            // BIG_DECIMAL使用浮点数值
            .featuresToEnable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
    }
}
