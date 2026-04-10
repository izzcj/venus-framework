package com.ale.venus.workflow.support;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * ID生成器支持
 *
 * @author Ale
 * @version 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IdGeneratorSupport {

    /**
     * ID生成器
     */
    private static IdGenerator idGenerator;

    /**
     * 设置ID生成器
     *
     * @param idGenerator ID生成器
     */
    static void setIdGenerator(IdGenerator idGenerator) {
        IdGeneratorSupport.idGenerator = idGenerator;
    }

    /**
     * 生成ID
     *
     * @return ID
     */
    public static String generateId() {
        return idGenerator.generate();
    }
}
