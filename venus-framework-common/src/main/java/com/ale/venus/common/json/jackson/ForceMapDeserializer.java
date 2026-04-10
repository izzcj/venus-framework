package com.ale.venus.common.json.jackson;

import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.exception.JsonDeserializerException;
import com.ale.venus.common.utils.CastUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.MapDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer;
import com.fasterxml.jackson.databind.type.MapType;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Map强制反序列化
 *
 * @author Ale
 * @version 1.0.0
 */
public class ForceMapDeserializer extends MapDeserializer {

    public ForceMapDeserializer(MapDeserializer src) {
        super(src);
    }

    @Override
    public Map<Object, Object> deserialize(JsonParser p, DeserializationContext context) throws IOException {
        // 不能序列化时
        if (!_valueInstantiator.canInstantiate()) {

            // 遍历构造器，寻找「包装父类」的构造器
            for (Constructor<?> constructor : this._valueClass.getDeclaredConstructors()) {
                if (constructor.getParameterTypes().length == 1 && constructor.getParameterTypes()[0] == _valueClass.getSuperclass()) {
                    MapType superType = context.getTypeFactory().constructMapType(
                        CastUtils.cast(
                            this._valueClass.getSuperclass()
                        ),
                        this._containerType.getKeyType(),
                        this._containerType.getContentType()
                    );

                    BeanDescription superTypeBeanDescription = context.getConfig().getClassIntrospector()
                        .forDeserialization(
                            context.getConfig(),
                            context.getTypeFactory().constructType(_valueClass.getSuperclass()),
                            context.getConfig()
                        );

                    Map<Object, Object> superTypeDeserialization = new MapDeserializer(
                        superType,
                        context.getFactory().findValueInstantiator(context, superTypeBeanDescription),
                        StdKeyDeserializer.forType(context.getTypeFactory().constructType(_valueClass.getSuperclass().getGenericInterfaces()[0]).getRawClass()),
                        context.findNonContextualValueDeserializer(context.getTypeFactory().constructType(_valueClass.getSuperclass().getGenericInterfaces()[1])),
                        context.getFactory().findTypeDeserializer(context.getConfig(), context.getTypeFactory().constructType(_valueClass.getSuperclass().getGenericInterfaces()[1]))
                    ).deserialize(p, context);

                    try {
                        boolean accessible = constructor.canAccess(null);

                        if (!accessible) {
                            constructor.setAccessible(true);
                        }

                        try {
                            return CastUtils.cast(
                                constructor.newInstance(superTypeDeserialization)
                            );
                        } finally {
                            if (!accessible) {
                                constructor.setAccessible(false);
                            }
                        }
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new JsonDeserializerException(StrUtil.format("创建实例[{}]失败", this._valueClass.getName()), e);
                    }
                }
            }
        }

        return super.deserialize(p, context);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) throws JsonMappingException {
        return new ForceMapDeserializer((MapDeserializer) super.createContextual(context, property));
    }
}
