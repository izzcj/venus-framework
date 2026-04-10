package com.ale.venus.workflow.query;

import com.ale.venus.workflow.entity.FlowDefinition;
import com.ale.venus.workflow.enumeration.FlowDefinitionState;

/**
 * 流程定义查询构建器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface DefinitionQuery extends BaseQuery<FlowDefinition, DefinitionQuery> {

    /**
     * 流程定义Key
     *
     * @param definitionKey 流程定义Key
     * @return this
     */
    DefinitionQuery definitionKey(String definitionKey);

    /**
     * 业务类型
     *
     * @param businessType 业务类型
     * @return this
     */
    DefinitionQuery businessType(String businessType);

    /**
     * 名称模糊匹配
     *
     * @param name 名称
     * @return this
     */
    DefinitionQuery nameLike(String name);

    /**
     * 是否发布
     *
     * @param published 是否发布
     * @return this
     */
    DefinitionQuery published(Boolean published);

    /**
     * 状态
     *
     * @param state 状态
     * @return this
     */
    DefinitionQuery state(FlowDefinitionState state);

}
