package com.ale.venus.common.json.fastjson.reader;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.ObjectReader;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

/**
 * 日期时间反序列化器
 *
 * @author Ale
 * @version 1.0.0
 */
@Component
public class LocalDateTimeObjectReader implements ObjectReader<LocalDateTime> {

    @Override
    public LocalDateTime readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
        var value = jsonReader.readString();
        if (StrUtil.isEmpty(value)) {
            return null;
        }

        return LocalDateTimeUtil.parse(value, DatePattern.NORM_DATETIME_PATTERN);
    }
}
