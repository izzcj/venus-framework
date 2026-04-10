package com.ale.venus.common.json.fastjson;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.writer.ObjectWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

/**
 * FastJson配置器
 *
 * @author Ale
 * @version 1.0.0
 */
@Component
@RequiredArgsConstructor
public class FastJsonConfigurer implements InitializingBean {

    /**
     * 序列化实现
     */
    private final ObjectProvider<ObjectWriter<?>> objectWriters;
    /**
     * 反序列化实现
     */
    private final ObjectProvider<ObjectReader<?>> objectReaders;

    @Override
    public void afterPropertiesSet() {
        for (ObjectWriter<?> objectWriter : this.objectWriters) {
            ResolvableType[] resolvableTypes = ResolvableType.forInstance(objectWriter)
                .getInterfaces();

            if (ArrayUtil.isNotEmpty(resolvableTypes) && resolvableTypes[0].hasGenerics()) {
                JSON.register(
                    resolvableTypes[0].resolveGeneric(0),
                    objectWriter
                );
            }
        }

        for (ObjectReader<?> objectReader : this.objectReaders) {
            ResolvableType[] resolvableTypes = ResolvableType.forInstance(objectReader)
                .getInterfaces();

            if (ArrayUtil.isNotEmpty(resolvableTypes) && resolvableTypes[0].hasGenerics()) {
                JSON.register(
                    resolvableTypes[0].resolveGeneric(0),
                    objectReader
                );
            }
        }
    }
}
