package com.ale.venus.common.json.jackson.deserializer;

import com.ale.venus.common.enumeration.BaseEnum;
import com.ale.venus.common.utils.CastUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.io.Serializable;

/**
 * 枚举反序列化器
 *
 * @param <T> 枚举值类型
 * @author Ale
 * @version 1.0.0
 */
public class EnumJsonDeserializer<T extends Serializable> extends StdDeserializer<BaseEnum<T>> {

    /**
     * 枚举类
     */
    private final Class<? extends BaseEnum<T>> clazz;

    protected EnumJsonDeserializer(Class<? extends BaseEnum<T>> clazz) {
        super(clazz);
        this.clazz = clazz;
    }

    @Override
    public BaseEnum<T> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        T value = CastUtils.cast(p.getValueAsString());

        return BaseEnum.getByValue(this.clazz, value);
    }
}
