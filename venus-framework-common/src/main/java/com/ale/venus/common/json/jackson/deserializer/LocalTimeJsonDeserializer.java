package com.ale.venus.common.json.jackson.deserializer;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import org.springframework.boot.jackson.JsonComponent;

import java.time.format.DateTimeFormatter;

/**
 * 时间反序列化器
 *
 * @author Ale
 * @version 1.0.0
 */
@JsonComponent
public class LocalTimeJsonDeserializer extends LocalTimeDeserializer {
    public LocalTimeJsonDeserializer() {
        super(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN));
    }
}
