package com.ale.venus.common.json.fastjson.writer;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 时间序列化器
 *
 * @author Ale
 * @version 1.0.0
 */
@Component
public class LocalTimeObjectWriter implements ObjectWriter<LocalTime> {

    @Override
    public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        jsonWriter.writeString(
            LocalDateTimeUtil.format(
                LocalDateTime.from((LocalTime) object),
                DatePattern.NORM_TIME_PATTERN
            )
        );
    }
}
