package com.ale.venus.common.json.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.stereotype.Component;

/**
 * 雪花ID序列化模块
 *
 * @author Ale
 * @version 1.0.0
 */
@Component
public class SnowflakeIdSerializerModule extends SimpleModule {

    @Override
    public String getModuleName() {
        return "SnowflakeIdSerializerModule";
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);

        context.addBeanSerializerModifier(
            new SnowflakeIdSerializerModifier()
        );
    }
}
