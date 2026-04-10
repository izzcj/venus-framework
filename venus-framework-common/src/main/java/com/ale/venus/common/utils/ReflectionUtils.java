package com.ale.venus.common.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.cache.CacheManager;
import com.ale.venus.common.exception.ReflectionException;
import com.ale.venus.common.support.ReflectionField;
import com.ale.venus.common.support.ReflectionMethod;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.objenesis.ObjenesisStd;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 反射工具类
 *
 * @author Ale
 * @version 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReflectionUtils {

    /**
     * 类字段缓存
     */
    private static final CacheManager<ClassFieldsCacheKey, List<ReflectionField>> CLASS_FIELDS_CACHE = CacheManager.newCache(ReflectionUtils.class);

    /**
     * 类中对象字段缓存
     */
    private static final CacheManager<ClassFieldsCacheKey, List<ReflectionField>> CLASS_OBJECT_FIELDS_CACHE = CacheManager.newCache(ReflectionUtils.class);

    /**
     * 类中对象字段Map缓存
     */
    private static final CacheManager<ClassFieldsCacheKey, Map<String, ReflectionField>> CLASS_OBJECT_FIELD_MAP_CACHE = CacheManager.newCache(ReflectionUtils.class);

    /**
     * 标有特定注解的类字段缓存
     */
    private static final CacheManager<ClassAnnotatedFieldsCacheKey, List<ReflectionField>> CLASS_ANNOTATED_FIELDS_CACHE = CacheManager.newCache(ReflectionUtils.class);

    /**
     * 类方法缓存
     */
    private static final CacheManager<ClassMethodCacheKey, ReflectionMethod> CLASS_METHOD_CACHE = CacheManager.newCache(ReflectionUtils.class);

    /**
     * 类单个字段缓存
     */
    private static final CacheManager<ClassFieldCacheKey, ReflectionField> CLASS_FIELD_CACHE = CacheManager.newCache(ReflectionUtils.class);

    /**
     * 对象实例化器
     */
    private static final ObjenesisStd OBJENESIS = new ObjenesisStd(true);

    record ClassFieldsCacheKey(Class<?> clazz, boolean includeParentFields) {
    }

    /**
     * 实例化一个类
     *
     * @param clazz 类
     * @return 类实例
     * @param <T> 类型
     */
    public static <T> T instantiate(Class<T> clazz) {
        return OBJENESIS.newInstance(clazz);
    }

    /**
     * 获取类中所有的字段
     *
     * @param clazz 类
     * @param includeParentFields 是否包含父级字段
     * @return 字段列表
     */
    public static List<ReflectionField> getClassFields(Class<?> clazz, boolean includeParentFields) {
        return CLASS_FIELDS_CACHE.computeIfAbsent(new ClassFieldsCacheKey(clazz, includeParentFields), __ -> {
            List<ReflectionField> classFields = Lists.newArrayListWithExpectedSize(32);
            Class<?> currentClass = clazz;
            MethodHandles.Lookup lookup;
            try {
                do {
                    lookup = MethodHandles.privateLookupIn(currentClass, MethodHandles.lookup());
                    Field[] fields = currentClass.getDeclaredFields();
                    if (ArrayUtil.isNotEmpty(fields)) {
                        for (Field field : fields) {
                            classFields.add(
                                new ReflectionField(field, lookup.unreflectVarHandle(field))
                            );
                        }
                    }
                    if (!includeParentFields) {
                        return classFields;
                    }
                    currentClass = currentClass.getSuperclass();
                } while (currentClass != null);
            } catch (IllegalAccessException e) {
                throw new ReflectionException("创建类[{}]的Lookup或属性句柄实例失败：{}", e, clazz.getSimpleName(), e.getMessage());
            }
            return Collections.unmodifiableList(classFields);
        });
    }

    /**
     * 获取类中所有对象字段（非静态）
     *
     * @param clazz 类
     * @param includeParentFields 是否包含父类字段
     * @return 非静态字段列表
     */
    public static List<ReflectionField> getClassObjectFields(Class<?> clazz, boolean includeParentFields) {
        return CLASS_OBJECT_FIELDS_CACHE.computeIfAbsent(new ClassFieldsCacheKey(clazz, includeParentFields), __ -> getClassFields(clazz, includeParentFields)
            .stream()
            .filter(reflectionField -> !Modifier.isStatic(reflectionField.field().getModifiers()))
            .toList()
        );
    }

    /**
     * 获取类中所有对象字段Map
     *
     * @param clazz 类
     * @param includeParentFields 是否包含父类字段
     * @return 非静态字段Map
     */
    public static Map<String, ReflectionField> getClassObjectFieldMap(Class<?> clazz, boolean includeParentFields) {
        return CLASS_OBJECT_FIELD_MAP_CACHE.computeIfAbsent(new ClassFieldsCacheKey(clazz, includeParentFields), __ -> getClassObjectFields(clazz, includeParentFields)
            .stream()
            .collect(
                Collectors.toMap(reflectionField -> reflectionField.field().getName(), Function.identity())
            )
        );
    }

    record ClassAnnotatedFieldsCacheKey(Class<?> clazz, Class<? extends Annotation> annotationClass) {
    }

    /**
     * 获取类中所有标有指定注解的字段
     *
     * @param clazz 类
     * @param annotationClass 注解类
     * @return 字段列表
     */
    public static List<ReflectionField> getClassAnnotatedFields(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        return CLASS_ANNOTATED_FIELDS_CACHE.computeIfAbsent(
            new ClassAnnotatedFieldsCacheKey(clazz, annotationClass),
            f -> getClassFields(clazz, true)
                .stream()
                .filter(field -> !Modifier.isStatic(field.field().getModifiers()) && AnnotatedElementUtils.hasAnnotation(field.field(), annotationClass))
                .toList()
        );
    }

    /**
     * 获取类中所有标有指定注解的字段
     *
     * @param clazz 类
     * @param annotationClasses 注解类集合
     * @return 字段列表
     */
    public static List<ReflectionField> getClassAnnotatedFields(Class<?> clazz, Set<Class<? extends Annotation>> annotationClasses) {
        return annotationClasses.stream()
            .map(annotationClass -> getClassAnnotatedFields(clazz, annotationClass))
            .flatMap(List::stream)
            .toList();
    }

    record ClassMethodCacheKey(Class<?> clazz, String methodName, Class<?>[] methodArgumentTypes) {
    }

    /**
     * 构建反射方法对象
     *
     * @param method 原始方法反射对象
     * @param clazz 类
     * @return 反射方法对象
     */
    private static ReflectionMethod buildReflectionMethod(Method method, String clazz) {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), MethodHandles.lookup());
            return new ReflectionMethod(method, lookup.unreflect(method));
        } catch (IllegalAccessException e) {
            throw new ReflectionException("创建类p{}]的Lookup或方法句柄实例失败：{}", e, clazz, e.getMessage());
        }
    }

    /**
     * 获取类中的方法
     *
     * @param clazz 类
     * @param methodName 方法名称
     * @param argumentTypes 方法参数类型数组
     * @return 方法对象
     */
    public static ReflectionMethod getMethod(Class<?> clazz, String methodName, Class<?>... argumentTypes) {
        return CLASS_METHOD_CACHE.computeIfAbsent(
            new ClassMethodCacheKey(clazz, methodName, argumentTypes),
            f -> {
                Method method = ReflectUtil.getMethod(clazz, methodName, argumentTypes);

                if (method == null) {
                    return null;
                }

                return buildReflectionMethod(method, clazz.getSimpleName());
            }
        );
    }

    /**
     * 获取类中的方法
     *
     * @param clazz 类
     * @param methodName 方法名称
     * @return 方法对象
     */
    public static ReflectionMethod getMethod(Class<?> clazz, String methodName) {
        return CLASS_METHOD_CACHE.computeIfAbsent(new ClassMethodCacheKey(clazz, methodName, null), __ -> {
            AtomicReference<Method> methodReference = new AtomicReference<>(null);
            org.springframework.util.ReflectionUtils.doWithMethods(clazz, methodReference::set, method -> method.getName().equals(methodName));

            if (methodReference.get() == null) {
                return null;
            }

            return buildReflectionMethod(methodReference.get(), clazz.getSimpleName());
        });
    }

    /**
     * 类中是否包含指定名称和参数的方法
     *
     * @param clazz 类
     * @param methodName 方法名称
     * @param argumentTypes 方法参数类型数组
     * @return bool
     */
    public static boolean hasMethod(Class<?> clazz, String methodName, Class<?>... argumentTypes) {
        return CLASS_METHOD_CACHE.has(new ClassMethodCacheKey(clazz, methodName, argumentTypes));
    }

    record ClassFieldCacheKey(Class<?> clazz, String fieldName) {
    }

    /**
     * 获取类中的字段根据字段名称
     *
     * @param clazz 类
     * @param fieldName 字段名称
     * @return 字段对象
     */
    public static ReflectionField getField(Class<?> clazz, String fieldName) {
        if (StrUtil.isBlank(fieldName)) {
            return null;
        }
        return CLASS_FIELD_CACHE.computeIfAbsent(new ClassFieldCacheKey(clazz, fieldName), __ -> {
            Field field = ReflectUtil.getField(clazz, fieldName);

            if (field == null) {
                return null;
            }

            try {
                MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(field.getDeclaringClass(), MethodHandles.lookup());
                return new ReflectionField(field, lookup.unreflectVarHandle(field));
            } catch (IllegalAccessException e) {
                throw new ReflectionException("创建类[{}]的Lookup或属性句柄实例失败：{}", e, clazz.getSimpleName(), e.getMessage());
            }
        });
    }

    /**
     * 类中是否包含指定名称字段
     *
     * @param clazz 类
     * @param fieldName 字段名称
     * @return bool
     */
    public static boolean hasField(Class<?> clazz, String fieldName) {
        return CLASS_FIELD_CACHE.has(new ClassFieldCacheKey(clazz, fieldName));
    }

    /**
     * 设置字段值
     *
     * @param field 反射字段对象
     * @param instance 实例对象
     * @param value 值
     */
    public static void setFieldValue(ReflectionField field, Object instance, Object value) {
        if (field == null) {
            return;
        }

        Class<?> targetType = field.field().getType();
        if (targetType.isAssignableFrom(value.getClass())) {
            field.setValue(instance, value);
            return;
        }
        field.setValue(
            instance,
            Convert.convert(targetType, value)
        );
    }
}

