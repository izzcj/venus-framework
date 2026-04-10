package com.ale.venus.common.json.jackson.serializer;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.boot.jackson.JsonComponent;

import java.time.format.DateTimeFormatter;

/**
 * 日期序列化器
 *
 * @author Ale
 * @version 1.0.0
 */
@JsonComponent
public class LocalDateJsonSerializer extends LocalDateSerializer {

    public LocalDateJsonSerializer() {
        super(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
    }

}
