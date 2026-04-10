package com.ale.venus.common.json.jackson;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import org.springframework.util.ClassUtils;

/**
 * 类型解析构建器
 *
 * @author Ale
 * @version 1.0.0
 */
public class TypeResolverBuilder extends ObjectMapper.DefaultTypeResolverBuilder {

    public TypeResolverBuilder(ObjectMapper.DefaultTyping t, PolymorphicTypeValidator ptv) {
        super(t, ptv);
    }

    @Override
    public ObjectMapper.DefaultTypeResolverBuilder withDefaultImpl(Class<?> defaultImpl) {
        return this;
    }

    @Override
    public boolean useForType(JavaType t) {
        if (t.isJavaLangObject()) {
            return true;
        }

        t = this.resolveArrayOrWrapper(t);

        if (t.isEnumType() || ClassUtils.isPrimitiveOrWrapper(t.getRawClass())) {
            return false;
        }

        // [databind#88] Should not apply to JSON tree models:
        return !TreeNode.class.isAssignableFrom(t.getRawClass());
    }

    /**
     * 解析数组项类型
     *
     * @param type Java类型
     * @return 数组项Java类型
     */
    private JavaType resolveArrayOrWrapper(JavaType type) {

        while (type.isArrayType()) {
            type = type.getContentType();
            if (type.isReferenceType()) {
                type = this.resolveArrayOrWrapper(type);
            }
        }

        while (type.isReferenceType()) {
            type = type.getReferencedType();
            if (type.isArrayType()) {
                type = this.resolveArrayOrWrapper(type);
            }
        }

        return type;
    }
}
