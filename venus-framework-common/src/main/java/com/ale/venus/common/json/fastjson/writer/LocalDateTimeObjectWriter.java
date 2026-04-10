package com.ale.venus.common.json.fastjson.writer;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

/**
 * 日期时间序列化器
 *
 * @author Ale
 * @version 1.0.0
 */
@Component
public class LocalDateTimeObjectWriter implements ObjectWriter<LocalDateTime> {

    @Override
    public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        jsonWriter.writeString(
            LocalDateTimeUtil.format(
                (LocalDateTime) object,
                DatePattern.NORM_DATETIME_PATTERN
            )
        );
    }
}
