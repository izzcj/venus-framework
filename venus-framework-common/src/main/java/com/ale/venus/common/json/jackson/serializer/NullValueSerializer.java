package com.ale.venus.common.json.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.cache.support.NullValue;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.Serial;

/**
 * 空值序列化器
 *
 * @author Ale
 * @version 1.0.0
 */
public class NullValueSerializer extends StdSerializer<NullValue> {

    @Serial
    private static final long serialVersionUID = -8240254865200182737L;
    /**
     * 类标识符
     */
    private final String classIdentifier;

    /**
     * @param classIdentifier can be {@literal null} and will be defaulted to {@code @class}.
     */
    public NullValueSerializer(@Nullable String classIdentifier) {
        super(NullValue.class);
        this.classIdentifier = StringUtils.hasText(classIdentifier) ? classIdentifier : "@class";
    }

    @Override
    public void serialize(NullValue value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField(this.classIdentifier, NullValue.class.getName());
        gen.writeEndObject();
    }

    @Override
    public void serializeWithType(NullValue value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        this.serialize(value, gen, serializers);
    }

}
