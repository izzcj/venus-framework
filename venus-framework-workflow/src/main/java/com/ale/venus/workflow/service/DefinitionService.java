package com.ale.venus.workflow.service;

import com.ale.venus.workflow.entity.FlowDefinition;

/**
 * 流程定义服务
 *
 * @author Ale
 * @version 1.0.0
 */
public interface DefinitionService {

    /**
     * 部署流程定义
     *
     * @param flowDefinition 流程定义
     * @return 流程定义ID
     */
    default String deploy(FlowDefinition flowDefinition) {
        return this.deploy(flowDefinition, false);
    }

    /**
     * 部署流程定义
     *
     * @param flowDefinition 流程定义
     * @param override       是否覆盖
     * @return 流程定义ID
     */
    String deploy(FlowDefinition flowDefinition, boolean override);

    /**
     * 更新流程定义
     *
     * @param flowDefinition 流程定义
     * @return 更新结果
     */
    boolean update(FlowDefinition flowDefinition);

    /**
     * 卸载流程定义
     *
     * @param id 流程定义ID
     * @return 删除结果
     */
    boolean undeploy(String id);

    /**
     * 删除流程定义
     *
     * @param id 流程定义ID
     * @return 删除流程定义的ID
     */
    String remove(String id);
}
