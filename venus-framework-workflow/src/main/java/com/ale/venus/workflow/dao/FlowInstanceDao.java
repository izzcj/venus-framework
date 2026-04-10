package com.ale.venus.workflow.dao;

import com.ale.venus.workflow.entity.FlowInstance;
import com.ale.venus.workflow.query.ActiveInstanceQuery;

/**
 * 流程实例数据访问层
 *
 * @author Ale
 * @version 1.0.0
 */
public interface FlowInstanceDao {

    /**
     * 创建流程实例查询构建器
     *
     * @return 流程实例查询构建器
     */
    ActiveInstanceQuery createActiveInstanceQuery();

    /**
     * 插入流程实例
     *
     * @param flowInstance 流程实例
     * @return 是否成功
     */
    boolean insert(FlowInstance flowInstance);

    /**
     * 根据流程实例ID查询流程实例
     *
     * @param instanceId 流程实例ID
     * @return 流程实例
     */
    FlowInstance selectById(String instanceId);

    /**
     * 根据业务ID和类型查询流程实例
     *
     * @param businessId   业务ID
     * @param businessType 业务类型
     * @return 流程实例
     */
    FlowInstance selectByBusinessIdAndType(String businessId, String businessType);

    /**
     * 更新流程实例
     *
     * @param flowInstance 流程实例
     * @return 是否成功
     */
    boolean updateById(FlowInstance flowInstance);

    /**
     * 删除流程实例
     *
     * @param instanceId 流程实例ID
     * @return 是否成功
     */
    boolean deleteById(String instanceId);
}
