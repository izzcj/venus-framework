package com.ale.venus.common.json.jackson.deserializer;

import com.ale.venus.common.enumeration.BaseEnum;
import com.ale.venus.common.enumeration.EnumInitializer;
import com.ale.venus.common.utils.CastUtils;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.stereotype.Component;

/**
 * 枚举反序列化模块
 *
 * @author Ale
 * @version 1.0.0
 */
@Component
@SuppressWarnings({"unchecked", "rawtypes"})
public class EnumJsonDeserializerModule extends SimpleModule implements EnumInitializer {

    @Override
    public String getModuleName() {
        return "EnumJsonDeserializerModule";
    }

    @Override
    public void initialize(Class<? extends BaseEnum<?>> enumClass) {
        this.addDeserializer(CastUtils.cast(enumClass), new EnumJsonDeserializer(enumClass));
    }
}
