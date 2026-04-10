package com.ale.venus.common.enumeration;

import cn.hutool.core.collection.CollectionUtil;
import com.ale.venus.common.support.Comment;
import com.ale.venus.common.support.Option;
import com.ale.venus.common.utils.CastUtils;
import com.ale.venus.common.utils.ClassUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.NoArgsConstructor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * 枚举持有器
 *
 * @author Ale
 * @version 1.0.0
 */
@Component
@NoArgsConstructor
public final class EnumHolder implements EnumInitializer {

    /**
     * 枚举选项
     */
    private static final List<Option> ENUM_OPTIONS = Lists.newArrayList();

    /**
     * 枚举值选项映射
     */
    private static final Map<String, List<Option>> ENUM_OPTIONS_MAPPING = Maps.newHashMap();

    /**
     * 读写锁
     */
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();


    @Override
    public void initialize(Class<? extends BaseEnum<?>> enumClass) {
        this.rwLock.writeLock().lock();

        try {
            ENUM_OPTIONS.add(
                Option.of(
                    Optional.ofNullable(AnnotationUtils.findAnnotation(enumClass, Comment.class))
                        .map(comment -> String.format("%s(%s)", comment.value(), enumClass.getSimpleName()))
                        .orElse(enumClass.getSimpleName()),
                    enumClass.getName(),
                    enumClass.getName()
                )
            );
            ENUM_OPTIONS_MAPPING.put(enumClass.getName(), buildEnumOptions(enumClass));
        } finally {
            this.rwLock.writeLock().unlock();
        }
    }

    /**
     * 获取枚举选项
     *
     * @return 枚举选项
     */
    public static List<Option> getEnumOptions() {
        return ENUM_OPTIONS;
    }

    /**
     * 获取枚举选项
     *
     * @param enumClassName 枚举类
     * @return 枚举选项
     */
    public static List<Option> getEnumOptions(String enumClassName) {
        List<Option> result = ENUM_OPTIONS_MAPPING.get(enumClassName);
        if (CollectionUtil.isEmpty(result)) {
            Class<?> enumClass = ClassUtils.loadClass(enumClassName);
            if (enumClass.isEnum() && BaseEnum.class.isAssignableFrom(enumClass)) {
                result = buildEnumOptions(CastUtils.cast(enumClass));
                ENUM_OPTIONS_MAPPING.put(enumClassName, result);
            }
        }
        return result;
    }

    /**
     * 构建枚举选项
     *
     * @param enumClass 枚举类
     * @return 枚举选项
     */
    private static List<Option> buildEnumOptions(Class<? extends BaseEnum<?>> enumClass) {
        BaseEnum<?>[] enumConstants = enumClass.getEnumConstants();
        return Arrays.stream(enumConstants)
            .map(
                enumConstant -> Option.of(
                    enumConstant.getMsg(),
                    enumConstant.getValue(),
                    Optional.ofNullable(AnnotationUtils.findAnnotation(enumClass, Comment.class))
                        .map(comment -> String.format("%s(%s)", comment.value(), enumClass.getSimpleName()))
                        .orElse(enumClass.getSimpleName())
                )
            )
            .collect(Collectors.toList());
    }
}
