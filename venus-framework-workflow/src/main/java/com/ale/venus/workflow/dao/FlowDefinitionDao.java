package com.ale.venus.workflow.dao;

import com.ale.venus.workflow.entity.FlowDefinition;
import com.ale.venus.workflow.query.DefinitionQuery;

import java.util.List;

/**
 * 流程定义数据访问层
 *
 * @author Ale
 * @version 1.0.0
 */
public interface FlowDefinitionDao {

    /**
     * 创建流程定义查询构建器
     *
     * @return 流程定义查询构建器
     */
    DefinitionQuery createDefinitionQuery();

    /**
     * 通过ID查询流程定义
     *
     * @param id 流程定义id
     * @return 流程定义
     */
    FlowDefinition selectById(String id);

    /**
     * 通过KEY查询流程定义
     *
     * @param key     流程定义key
     * @param tenantId 租户id
     * @return 流程定义
     */
    FlowDefinition selectByKey(String key, String tenantId);

    /**
     * 通过类型查询流程定义列表
     *
     * @param type     流程定义id
     * @param tenantId 租户id
     * @return 列表
     */
    List<FlowDefinition> selectListByBusinessType(String type, String tenantId);

    /**
     * 插入流程定义
     *
     * @param flowDefinition 流程定义
     * @return 插入结果
     */
    boolean insert(FlowDefinition flowDefinition);

    /**
     * 通过ID更新流程定义
     *
     * @param flowDefinition 流程定义
     * @return 更新结果
     */
    boolean updateById(FlowDefinition flowDefinition);

    /**
     * 通过KEY更新流程定义
     *
     * @param flowDefinition 流程定义
     * @param key            流程定义key
     * @param tenantId       租户id
     * @return 更新结果
     */
    boolean updateByKey(FlowDefinition flowDefinition, String key, String tenantId);

    /**
     * 删除流程定义
     *
     * @param id 流程定义id
     * @return 删除结果
     */
    boolean deleteById(String id);

    /**
     * 判断流程定义是否存在
     *
     * @param id 流程定义id
     * @return 是否存在
     */
    boolean exist(String id);

    /**
     * 判断流程定义是否存在
     *
     * @param id 流程定义id
     * @return 是否存在
     */
    default boolean notExist(String id) {
        return !this.exist(id);
    }
}
