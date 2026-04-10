package com.ale.venus.core.spring.converter;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.constants.StringConstants;
import com.ale.venus.common.enumeration.BaseEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

/**
 * 字符串转数组转换器
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
@Component
public class StringToArrayConverter implements GenericConverter {

    /**
     * 支持的数组或集合组件类型
     */
    private static final Set<Class<?>> SUPPORTED_COMPONENT_CLASSES = Set.of(
        int.class,
        Integer.class,
        long.class,
        Long.class,
        BigDecimal.class,
        String.class
    );

    @Nullable
    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Set.of(
            new ConvertiblePair(String.class, int[].class),
            new ConvertiblePair(String.class, Integer[].class),
            new ConvertiblePair(String.class, long[].class),
            new ConvertiblePair(String.class, Long[].class),
            new ConvertiblePair(String.class, BigDecimal[].class),
            new ConvertiblePair(String.class, String[].class),
            new ConvertiblePair(String.class, Collection.class)
        );
    }

    @Nullable
    @Override
    public Object convert(@Nullable Object source, @NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
        String value = (String) source;
        if (StrUtil.isBlank(value)) {
            return null;
        }

        String[] tokens = value.split(StringConstants.COMMA);
        if (targetType.isArray()) {
            Class<?> componentType = targetType.getType().getComponentType();

            if (SUPPORTED_COMPONENT_CLASSES.contains(componentType)) {
                return Arrays.stream(tokens).map(
                    token -> {
                        if (String.class.isAssignableFrom(componentType)) {
                            return token;
                        }

                        return Convert.convert(
                            componentType,
                            token
                        );
                    }
                ).toArray();
            } else {
                log.warn("字符串转Array的组件不支持类型：{}", componentType.getName());
            }
        }

        if (targetType.isCollection()) {
            Class<?> componentType = targetType.getResolvableType().resolveGeneric(0);
            if (componentType == null) {
                return Arrays.asList(tokens);
            }

            if (SUPPORTED_COMPONENT_CLASSES.contains(componentType) || BaseEnum.class.isAssignableFrom(componentType)) {
                return Arrays.stream(tokens).map(
                    token -> {
                        if (String.class.isAssignableFrom(componentType)) {
                            return token;
                        }

                        return Convert.convert(
                            componentType,
                            token
                        );
                    }
                ).toList();
            } else {
                log.warn("字符串转Collection的泛型不支持类型：{}", componentType.getName());
            }
        }

        return null;
    }
}
