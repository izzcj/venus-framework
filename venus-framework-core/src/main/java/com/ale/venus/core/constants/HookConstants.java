package com.ale.venus.core.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 钩子常量
 *
 * @author Ale
 * @version 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HookConstants {

    /**
     * 查询参数-key
     */
    public static final String QUERY_KEY = "query";

    /**
     * 实体BO-key
     */
    public static final String ENTITY_BO_KEY = "entityBO";

    /**
     * 实体BO列表-key
     */
    public static final String ENTITY_BO_LIST_KEY = "entityBOList";

    /**
     * 实体BO映射关系-key
     */
    public static final String ENTITY_BO_MAP_KEY = "entityBOMap";

    /**
     * 旧实体-key
     */
    public static final String OLD_ENTITY_KEY = "oldEntity";

    /**
     * 旧实体映射关系-key
     */
    public static final String OLD_ENTITY_MAP_KEY = "oldEntityMap";
}
