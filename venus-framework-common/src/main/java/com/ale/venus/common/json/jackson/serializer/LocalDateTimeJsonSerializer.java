package com.ale.venus.common.json.jackson.serializer;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.jackson.JsonComponent;

import java.time.format.DateTimeFormatter;

/**
 * 日期时间序列化器
 *
 * @author Ale
 * @version 1.0.0
 */
@JsonComponent
public class LocalDateTimeJsonSerializer extends LocalDateTimeSerializer {

    public LocalDateTimeJsonSerializer() {
        super(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN));
    }

}
