package com.ale.venus.common.json.jackson.deserializer;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.springframework.boot.jackson.JsonComponent;

import java.time.format.DateTimeFormatter;

/**
 * 日期时间反序列化器
 *
 * @author Ale
 * @version 1.0.0
 */
@JsonComponent
public class LocalDateTimeJsonDeserializer extends LocalDateTimeDeserializer {
    public LocalDateTimeJsonDeserializer() {
        super(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN));
    }
}
