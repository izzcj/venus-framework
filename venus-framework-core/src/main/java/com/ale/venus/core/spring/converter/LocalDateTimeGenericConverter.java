package com.ale.venus.core.spring.converter;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

/**
 * 日期时间通用转换器
 *
 * @author Ale
 * @version 1.0.0
 */
@Component
public class LocalDateTimeGenericConverter implements GenericConverter {

    @Nullable
    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Set.of(
            new ConvertiblePair(String.class, LocalDateTime.class),
            new ConvertiblePair(String.class, LocalDate.class),
            new ConvertiblePair(String.class, LocalTime.class)
        );
    }

    @Nullable
    @Override
    public Object convert(@Nullable Object source, @NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
        String value = (String) source;
        if (StrUtil.isBlank(value)) {
            return null;
        }

        Class<?> targetClass = targetType.getObjectType();
        if (LocalDateTime.class.isAssignableFrom(targetClass)) {
            return LocalDateTimeUtil.parse(value, DatePattern.NORM_DATETIME_PATTERN);
        } else if (LocalDate.class.isAssignableFrom(targetClass)) {
            return LocalDateTimeUtil.parse(value, DatePattern.NORM_DATE_PATTERN);
        } else if (LocalTime.class.isAssignableFrom(targetClass)) {
            return LocalDateTimeUtil.parse(value, DatePattern.NORM_TIME_PATTERN);
        }

        return null;
    }
}
