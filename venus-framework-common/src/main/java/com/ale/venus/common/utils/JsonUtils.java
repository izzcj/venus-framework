package com.ale.venus.common.utils;

import cn.hutool.core.collection.ListUtil;
import com.ale.venus.common.exception.UtilException;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;
import java.util.List;

/**
 * JSON工具类
 *
 * @author Ale
 * @version 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonUtils {

    /**
     * ObjectMapper
     */
    private static ObjectMapper objectMapper;
    /**
     * 美化格式的Writer
     */
    private static ObjectWriter prettyObjectWriter;

    /**
     * 初始化ObjectMapper
     *
     * @param objectMapper ObjectMapper
     */
    static void initialize(ObjectMapper objectMapper) {
        JsonUtils.objectMapper = objectMapper;
        JsonUtils.prettyObjectWriter = objectMapper.writerWithDefaultPrettyPrinter();
    }

    static {
        // 特性配置
        JSON.config(JSONReader.Feature.FieldBased);
        JSON.config(JSONReader.Feature.IgnoreSetNullValue);
        JSON.config(JSONReader.Feature.UseNativeObject);
        JSON.config(JSONReader.Feature.UseDefaultConstructorAsPossible);
        JSON.config(JSONWriter.Feature.FieldBased);
        JSON.config(JSONWriter.Feature.WriteBigDecimalAsPlain);
        JSON.config(JSONWriter.Feature.WriteNulls);
        JSON.config(JSONWriter.Feature.WriteMapNullValue);
        JSON.config(JSONWriter.Feature.WriteEnumsUsingName);
    }

    /**
     * 对象转JSON字符串
     *
     * @param value 对象
     * @return JSON字符串
     */
    public static String toJson(Object value) {
        if (objectMapper == null) {
            return JSON.toJSONString(value);
        }

        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new UtilException("JSON序列化对象[{}]失败：{}", e, value.getClass().getSimpleName(), e.getMessage());
        }
    }

    /**
     * 对象转美化后的JSON字符串
     *
     * @param value 对象
     * @return 美化JSON字符串
     */
    public static String toPrettyJson(Object value) {
        if (prettyObjectWriter == null) {
            return JSON.toJSONString(value, JSONWriter.Feature.PrettyFormat);
        }

        try {
            return prettyObjectWriter.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new UtilException("JSON序列化（美化）对象[{}]失败：{}", e, value.getClass().getSimpleName(), e.getMessage());
        }
    }

    /**
     * 将JSON字符串转为对象
     *
     * @param json  json字符串
     * @param clazz 结果类
     * @param <T> 结果类型
     * @return 对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (objectMapper == null) {
            return JSON.parseObject(json, clazz);
        }

        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new UtilException("JSON反序列化对象[{}]失败：{}", e, clazz.getSimpleName(), e.getMessage());
        }
    }

    /**
     * 将JSON字符串转为对象列表
     *
     * @param json          json字符串
     * @param componentType 列表元素类型
     * @return 列表
     * @param <T> 列表元素类型
     */
    public static <T> List<T> fromJsonList(String json, Class<T> componentType) {
        return ListUtil.toList(fromJsonArray(json, componentType));
    }

    /**
     * 将JSON字符串转为对象数组
     *
     * @param json          json字符串
     * @param componentType 数组类型
     * @return 数组
     * @param <T> 数组类型
     */
    public static <T> T[] fromJsonArray(String json, Type componentType) {
        return fromJson(json, TypeUtils.arrayOf(componentType));
    }

    /**
     * 将JSON字符串转为参数化对象
     *
     * @param json    json字符串
     * @param rawType 原类型
     * @param typeArguments 泛型参数
     * @return 对象
     * @param <T> 结果类型
     */
    public static <T> T fromJson(String json, Type rawType, Type... typeArguments) {
        return fromJson(json, TypeUtils.newParameterizedTypeWithOwner(null, rawType, typeArguments));
    }

    /**
     * 将JSON字符串转为对象
     *
     * @param json json字符串
     * @param type 对象类型
     * @param <T> 结果类型
     * @return 对象
     */
    public static <T> T fromJson(String json, Type type) {
        try {
            return CastUtils.cast(objectMapper.readValue(json, new TypeReferenceImpl(type)));
        } catch (JsonProcessingException e) {
            throw new UtilException("JSON反序列化对象[{}]失败：{}", e, type.getTypeName(), e.getMessage());
        }
    }

    /**
     * 类型引用实现
     */
    @RequiredArgsConstructor
    private static final class TypeReferenceImpl extends TypeReference<Object> {
        /**
         * 类型
         */
        private final Type type;

        /**
         * 获取类型
         *
         * @return 类型
         */
        @Override
        public Type getType() {
            return this.type;
        }
    }
}
