package com.ale.venus.common.json.jackson.deserializer;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import org.springframework.boot.jackson.JsonComponent;

import java.time.format.DateTimeFormatter;

/**
 * 日期反序列化器
 *
 * @author Ale
 * @version 1.0.0
 */
@JsonComponent
public class LocalDateJsonDeserializer extends LocalDateDeserializer {
    public LocalDateJsonDeserializer() {
        super(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
    }
}
