package com.ale.venus.workflow.support;

import cn.hutool.core.util.IdUtil;

/**
 * 雪花id生成器
 *
 * @author Ale
 * @version 1.0.0
 */
public class SnowflakeIdGenerator implements IdGenerator {

    @Override
    public String generate() {
        return IdUtil.getSnowflakeNextIdStr();
    }
}
