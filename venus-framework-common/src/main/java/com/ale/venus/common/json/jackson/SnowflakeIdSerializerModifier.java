package com.ale.venus.common.json.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.util.List;

/**
 * Jackson雪花ID序列化修改器
 *
 * @author Ale
 * @version 1.0.0
 */
public class SnowflakeIdSerializerModifier extends BeanSerializerModifier {

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        for (BeanPropertyWriter writer : beanProperties) {

            // Long 类型 + 名字以 "id" 结尾或创建、修改人，自动转成字符串
            boolean needToString = writer.getType().getRawClass() == Long.class && writer.getName().toLowerCase().endsWith("id")
                || "updateBy".equals(writer.getName())
                || "createBy".equals(writer.getName());
            if (needToString) {
                writer.assignSerializer(ToStringSerializer.instance);
            }
        }
        return beanProperties;
    }
}
