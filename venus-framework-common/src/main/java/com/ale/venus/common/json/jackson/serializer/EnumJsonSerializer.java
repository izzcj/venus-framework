package com.ale.venus.common.json.jackson.serializer;

import com.ale.venus.common.constants.VenusConstants;
import com.ale.venus.common.enumeration.BaseEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.io.Serializable;

/**
 * 枚举序列化器
 *
 * @author Ale
 * @version 1.0.0
 */
@JsonComponent
@SuppressWarnings("rawtypes")
public class EnumJsonSerializer extends StdSerializer<BaseEnum> {

    public EnumJsonSerializer() {
        super(BaseEnum.class);
    }

    @Override
    public void serialize(BaseEnum value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        Serializable originalValue = value.getValue();
        switch (originalValue) {
            case String stringValue -> gen.writeString(stringValue);
            case Integer intValue -> gen.writeNumber(intValue);
            case Long longValue -> gen.writeNumber(longValue);
            case Double doubleValue -> gen.writeNumber(doubleValue);
            case Float floatValue -> gen.writeNumber(floatValue);
            default -> gen.writeString(originalValue.toString());
        }
        gen.writeStringField(gen.getOutputContext().getCurrentName() + VenusConstants.ENUM_NAME_PROPERTY_SUFFIX, value.getMsg());
    }
}
