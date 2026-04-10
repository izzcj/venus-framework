package com.ale.venus.common.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.nio.charset.StandardCharsets;

/**
 * Redis通用自动配置
 *
 * @author Ale
 * @version 1.0.0
 */
@AutoConfiguration
@AutoConfigureBefore(RedisAutoConfiguration.class)
@AutoConfigureAfter(JacksonAutoConfiguration.class)
public class RedisCommonAutoConfiguration {

    /**
     * Kryo RedisTemplate Bean
     *
     * @param connectionFactory Redis连接工厂
     * @return RedisTemplate Bean
     */
    @Bean
    public RedisTemplate<String, Object> kryoRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer(StandardCharsets.UTF_8);
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        GenericKryoRedisSerializer kryoValueSerializer = new GenericKryoRedisSerializer();

        redisTemplate.setValueSerializer(kryoValueSerializer);
        redisTemplate.setHashValueSerializer(kryoValueSerializer);

        return redisTemplate;
    }

    /**
     * Json RedisTemplate Bean
     *
     * @param connectionFactory   Redis连接工厂
     * @param objectMapperBuilder ObjectMapper构建器
     * @return RedisTemplate Bean
     */
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, Jackson2ObjectMapperBuilder objectMapperBuilder) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer(StandardCharsets.UTF_8);
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        ObjectMapper objectMapper = objectMapperBuilder.createXmlMapper(false).build();
        GenericJsonRedisSerializer jsonRedisSerializer = new GenericJsonRedisSerializer(objectMapper, null);

        redisTemplate.setValueSerializer(jsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jsonRedisSerializer);

        return redisTemplate;
    }
}
