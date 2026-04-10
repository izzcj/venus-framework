package com.ale.venus.common.redis;

import cn.hutool.core.util.ArrayUtil;
import com.ale.venus.common.json.jackson.TypeResolverBuilder;
import com.ale.venus.common.json.jackson.serializer.NullValueSerializer;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.jspecify.annotations.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Jackson Redis序列化器
 *
 * @author Ale
 * @version 1.0.0
 */
public class GenericJsonRedisSerializer implements RedisSerializer<Object> {

    /**
     * 空字节数组常量
     */
    private static final byte[] EMPTY_ARRAY = new byte[0];
    /**
     * 对象映射器
     */
    private final ObjectMapper mapper;

    /**
     * Creates {@link org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer} and configures {@link ObjectMapper} for default typing using the
     * given {@literal name}. In case of an {@literal empty} or {@literal null} String the default
     * {@link JsonTypeInfo.Id#CLASS} will be used.
     *
     * @param classPropertyTypeName Name of the JSON property holding type information. Can be {@literal null}.
     * @see ObjectMapper#activateDefaultTypingAsProperty(PolymorphicTypeValidator, ObjectMapper.DefaultTyping, String)
     * @see ObjectMapper#activateDefaultTyping(PolymorphicTypeValidator, ObjectMapper.DefaultTyping, JsonTypeInfo.As)
     */
    public GenericJsonRedisSerializer(@NonNull ObjectMapper mapper, @Nullable String classPropertyTypeName) {
        Assert.notNull(mapper, "ObjectMapper must not be null!");
        this.mapper = mapper;

        configDefaultTyping(this.mapper, classPropertyTypeName);
    }

    /**
     * 设置序列化类型处理
     *
     * @param objectMapper          the object mapper to customize.
     * @param classPropertyTypeName name of the type property. Defaults to {@code @class} if {@literal null}/empty.
     */
    public static void configDefaultTyping(ObjectMapper objectMapper, @Nullable String classPropertyTypeName) {
        registerNullValueSerializer(objectMapper, classPropertyTypeName);

        StdTypeResolverBuilder typer = new TypeResolverBuilder(
            ObjectMapper.DefaultTyping.NON_FINAL_AND_ENUMS,
            objectMapper.getPolymorphicTypeValidator()
        );
        typer = typer.init(JsonTypeInfo.Id.CLASS, null);
        typer = typer.inclusion(JsonTypeInfo.As.PROPERTY);

        if (StringUtils.hasText(classPropertyTypeName)) {
            typer = typer.typeProperty(classPropertyTypeName);
        }
        objectMapper.setDefaultTyping(typer);
    }

    /**
     * Register {@link NullValueSerializer} in the given {@link ObjectMapper} with an optional
     * {@code classPropertyTypeName}. This method should be called by code that customizes
     * {@link org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer} by providing an external {@link ObjectMapper}.
     *
     * @param objectMapper the object mapper to customize.
     * @param classPropertyTypeName name of the type property. Defaults to {@code @class} if {@literal null}/empty.
     * @since 2.2
     */
    public static void registerNullValueSerializer(ObjectMapper objectMapper, @Nullable String classPropertyTypeName) {
        objectMapper.registerModule(new SimpleModule().addSerializer(new NullValueSerializer(classPropertyTypeName)));
    }

    @Nullable
    @Override
    public byte[] serialize(@Nullable Object source) throws SerializationException {

        if (source == null) {
            return EMPTY_ARRAY;
        }

        try {
            return this.mapper.writeValueAsBytes(source);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Could not write JSON: " + e.getMessage(), e);
        }
    }

    @Nullable
    @Override
    public Object deserialize(@Nullable byte[] source) throws SerializationException {
        return this.deserialize(source, Object.class);
    }

    /**
     * 反序列化
     *
     * @param source can be {@literal null}.
     * @param type must not be {@literal null}.
     * @return {@literal null} for empty source.
     * @param <T> 对象类型
     * @throws SerializationException 反序列化异常
     */
    @Nullable
    public <T> T deserialize(@Nullable byte[] source, Class<T> type) throws SerializationException {

        Assert.notNull(
            type,
            "Deserialization type must not be null! Please provide Object.class to make use of Jackson2 default typing."
        );

        if (ArrayUtil.isEmpty(source)) {
            return null;
        }

        try {
            return this.mapper.readValue(source, type);
        } catch (Exception e) {
            throw new SerializationException("Could not read JSON: " + e.getMessage(), e);
        }
    }
}
