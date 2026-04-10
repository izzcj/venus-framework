package com.ale.venus.common.json.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.std.MapDeserializer;
import com.fasterxml.jackson.databind.type.MapType;

/**
 * 强制反序列化
 *
 * @author Ale
 * @version 1.0.0
 */
public class ForceCreatorBeanDeserializerModifier extends BeanDeserializerModifier {

    @Override
    public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDesc, BeanDeserializerBuilder builder) {
        ValueInstantiator valueInstantiator = builder.getValueInstantiator();
        Class<?> beanClass = beanDesc.getBeanClass();

        boolean forceCreationTarget = !beanClass.isPrimitive()
            && beanClass != String.class
            && beanDesc.findDefaultConstructor() == null
            && this.isNotPossibleInstantiation(valueInstantiator);

        if (forceCreationTarget) {
            builder.setValueInstantiator(ForceValueInstantiator.getInstance(beanClass));
        }

        return builder;
    }

    /**
     * 判断一个对象是否可能没办法实例化
     *
     * @param valueInstantiator 默认实例化器
     * @return bool
     */
    private boolean isNotPossibleInstantiation(ValueInstantiator valueInstantiator) {
        return !(valueInstantiator.canCreateUsingDelegate()
            || valueInstantiator.canCreateUsingArrayDelegate()
            || valueInstantiator.canCreateFromObjectWith());
    }

    @Override
    public JsonDeserializer<?> modifyMapDeserializer(DeserializationConfig config, MapType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        return new ForceMapDeserializer((MapDeserializer) deserializer);
    }
}
