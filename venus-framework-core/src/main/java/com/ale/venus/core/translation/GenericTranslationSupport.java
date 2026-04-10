package com.ale.venus.core.translation;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpUtil;
import com.ale.venus.common.constants.StringConstants;
import com.ale.venus.common.constants.VenusConstants;
import com.ale.venus.common.support.PatchData;
import com.ale.venus.common.support.ReflectionField;
import com.ale.venus.common.utils.ReflectionUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 通用翻译支持
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenericTranslationSupport {

    /**
     * 通用翻译器
     */
    private static final List<GenericTranslator> GENERIC_TRANSLATORS = Lists.newArrayList();

    /**
     * 注册通用翻译器
     *
     * @param genericTranslator 通用翻译器
     */
    static void registerGenericTranslator(GenericTranslator genericTranslator) {
        GENERIC_TRANSLATORS.add(genericTranslator);
    }

    /**
     * 处理通用翻译操作
     *
     * @param instance 对象实例
     */
    public static void translate(Object instance) {
        if (CollectionUtil.isEmpty(GENERIC_TRANSLATORS)) {
            log.warn("通用翻译处理跳过：没有通用翻译Bean，请检查是否在程序启动过程中过早的查询或翻译数据？");
            return;
        }

        List<ReflectionField> translationFields = ReflectionUtils.getClassAnnotatedFields(instance.getClass(), Set.of(TranslationField.class, TranslationFields.class));

        if (CollectionUtil.isEmpty(translationFields)) {
            return;
        }

        for (ReflectionField reflectionField : translationFields) {
            translate(reflectionField, instance);
        }
    }

    /**
     * 执行翻译操作
     *
     * @param type   数据类型
     * @param params 参数
     * @param value  数据值
     * @return 翻译后的值
     */
    @Nullable
    public static String translate(String type, Map<String, Object> params, String value) {
        for (GenericTranslator genericTranslator : GENERIC_TRANSLATORS) {
            if (genericTranslator.supports(type)) {
                return genericTranslator.translate(type, params, value);
            }
        }

        log.warn("通用数据翻译未能找到类型[{}]的翻译器实现，数据值[{}]无法被翻译，请检查", type, value);
        return null;
    }

    /**
     * 执行通用数据翻译操作
     *
     * @param field    反射字段对象
     * @param instance 对象实例
     */
    private static void translate(ReflectionField field, Object instance) {
        ResolvableType type = ResolvableType.forField(field.field());
        boolean isCollection = Collection.class.isAssignableFrom(type.toClass());

        List<String> values = Lists.newArrayList();
        if (isCollection) {
            List<Object> valueList = field.getValue(instance);
            if (valueList == null || valueList.isEmpty()) {
                return;
            }
            for (Object object : valueList) {
                if (object instanceof String stringValue) {
                    values.add(stringValue);
                    continue;
                }
                values.add(object.toString());
            }
        } else {
            Object value = field.getValue(instance);
            if (value == null) {
                return;
            }
            if (value instanceof String stringValue) {
                values.add(stringValue);
            } else {
                values.add(value.toString());
            }
        }

        TranslationFields translationFields = field.field().getAnnotation(TranslationFields.class);
        if (translationFields != null) {
            for (TranslationField translationField : translationFields.value()) {
                processTranslate(
                    instance,
                    translationField.type(),
                    mergeParams(Maps.newHashMap(), translationField.params()),
                    parseTranslateFieldName(field, translationField),
                    values
                );
            }
            return;
        }

        TranslationField translationField = field.field().getAnnotation(TranslationField.class);
        processTranslate(
            instance,
            translationField.type(),
            mergeParams(Maps.newHashMap(), translationField.params()),
            parseTranslateFieldName(field, translationField),
            values
        );
    }

    /**
     * 解析通用数据翻译字段名称
     *
     * @param field            反射字段对象
     * @param translationField 通用数据翻译字段注解
     * @return 通用数据翻译字段名称
     */
    private static String parseTranslateFieldName(ReflectionField field, TranslationField translationField) {
        String fieldName = field.field().getName();
        String translatedValueFieldName = translationField.field();
        if (StrUtil.isBlank(translatedValueFieldName)) {
            if (fieldName.endsWith("Id")) {
                translatedValueFieldName = StrUtil.strip(fieldName, null, "Id");
            } else {
                translatedValueFieldName = fieldName;
            }
            translatedValueFieldName += VenusConstants.GENERIC_TRANSLATION_NAME_PROPERTY_SUFFIX;
        }
        return translatedValueFieldName;
    }

    /**
     * 合并参数
     *
     * @param commonParams 公共参数
     * @param params       特定参数字符串
     * @return 合并后的参数
     */
    private static Map<String, Object> mergeParams(Map<String, Object> commonParams, String params) {
        if (StrUtil.isNotBlank(params)) {
            for (Map.Entry<String, String> entry : HttpUtil.decodeParamMap(params, StandardCharsets.UTF_8).entrySet()) {
                if (!ObjectUtils.isEmpty(entry.getValue()) || !commonParams.containsKey(entry.getKey())) {
                    commonParams.put(entry.getKey(), entry.getValue());
                }
            }
        }

        return commonParams;
    }

    /**
     * 处理翻译
     *
     * @param instance                 对象实例
     * @param type                     数据类型
     * @param params                   参数
     * @param translatedValueFieldName 翻译值字段名称
     * @param values                   数据值
     */
    private static void processTranslate(Object instance, String type, Map<String, Object> params, String translatedValueFieldName, List<String> values) {
        String translatedValue = values.stream()
            .map(value ->
                Optional.ofNullable(
                        translate(
                            type,
                            params,
                            value
                        )
                    )
                    .filter(StrUtil::isNotBlank)
                    .orElse(value)
            )
            .map(value -> StrUtil.blankToDefault(value, "unknown"))
            .collect(Collectors.joining(StringConstants.COMMA));
        if (StrUtil.isBlank(translatedValue)) {
            return;
        }
        ReflectionField translatedValueField = ReflectionUtils.getField(instance.getClass(), translatedValueFieldName);
        if (translatedValueField == null) {
            log.warn("通用数据翻译结果[{} -> {}]回写失败：类[{}]中不存在属性[{}]", translatedValue, translatedValue, instance.getClass().getName(), translatedValueFieldName);
            return;
        }
        translatedValueField.setValue(instance, translatedValue);
    }

    /**
     * 构建翻译补丁数据
     *
     * @param value     翻译值
     * @param patchType 补丁类型
     * @param <T> 翻译值类型
     * @return 翻译补丁数据
     */
    public static <T> PatchData<T> buildPatchData(T value, TranslationPatchType patchType) {
        Set<T> add = Collections.emptySet();
        Set<T> update = Collections.emptySet();
        Set<T> delete = Collections.emptySet();

        switch (patchType) {
            case TranslationPatchType.ADD:
                add = Collections.singleton(value);
                break;
            case TranslationPatchType.CHANGE:
                update = Collections.singleton(value);
                break;
            case TranslationPatchType.REMOVE:
                delete = Collections.singleton(value);
                break;
            default:
                throw new IllegalArgumentException("未知的补丁类型: " + patchType);
        }
        return PatchData.of(add, update, delete);
    }

    /**
     * 发布翻译映射值更新事件
     *
     * @param type 数据类型
     */
    public static void publishUpdateEvent(String type) {
        SpringUtil.publishEvent(
            new GenericTranslationMappingDataUpdateEvent(
                type
            )
        );
    }

    /**
     * 发布翻译映射值更新事件
     *
     * @param type 数据类型
     * @param params 参数
     */
    public static void publishUpdateEvent(String type, Map<String, Object> params) {
        SpringUtil.publishEvent(
            new GenericTranslationMappingDataUpdateEvent(
                type,
                params
            )
        );
    }

    /**
     * 发布翻译映射值更新事件
     *
     * @param type 数据类型
     * @param patchData 补丁数据
     */
    public static void publishUpdateEvent(String type, PatchData<Pair<String, String>> patchData) {
        SpringUtil.publishEvent(
            new GenericTranslationMappingDataUpdateEvent(
                type,
                patchData
            )
        );
    }

    /**
     * 发布翻译映射值更新事件
     *
     * @param type 数据类型
     * @param params 参数
     * @param patchData 补丁数据
     */
    public static void publishUpdateEvent(String type, Map<String, Object> params, PatchData<Pair<String, String>> patchData) {
        SpringUtil.publishEvent(
            new GenericTranslationMappingDataUpdateEvent(
                type,
                params,
                patchData
            )
        );
    }
}
