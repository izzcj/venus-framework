package com.ale.venus.workflow.dao;

import com.ale.venus.workflow.entity.FlowHistoryInstance;
import com.ale.venus.workflow.query.HistoryInstanceQuery;

import java.util.List;

/**
 * 历史流程实例数据访问层
 *
 * @author Ale
 * @version 1.0.0
 */
public interface FlowHistoryInstanceDao {

    /**
     * 创建历史流程实例查询构建器
     *
     * @return 历史流程实例查询构建器
     */
    HistoryInstanceQuery createHistoryInstanceQuery();

    /**
     * 根据ID查询历史流程实例
     *
     * @param id 历史流程实例ID
     * @return 历史流程实例
     */
    FlowHistoryInstance selectById(String id);

    /**
     * 根据业务ID查询历史流程实例
     *
     * @param businessId 业务ID
     * @return 历史流程实例列表
     */
    List<FlowHistoryInstance> selectByBusinessId(String businessId);

    /**
     * 根据业务类型查询历史流程实例
     *
     * @param businessType 业务类型
     * @return 历史流程实例列表
     */
    List<FlowHistoryInstance> selectByBusinessType(String businessType);

    /**
     * 插入历史流程实例
     *
     * @param historyInstance 历史流程实例
     * @return 结果
     */
    boolean insert(FlowHistoryInstance historyInstance);

    /**
     * 更新历史流程实例
     *
     * @param historyInstance 历史流程实例
     * @return 更新结果
     */
    boolean updateById(FlowHistoryInstance historyInstance);

}
