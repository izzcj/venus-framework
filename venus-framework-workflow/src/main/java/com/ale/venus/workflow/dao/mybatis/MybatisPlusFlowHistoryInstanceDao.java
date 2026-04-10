package com.ale.venus.workflow.dao.mybatis;

import com.ale.venus.workflow.dao.FlowHistoryInstanceDao;
import com.ale.venus.workflow.dao.mybatis.mapper.FlowHistoryInstanceMapper;
import com.ale.venus.workflow.entity.FlowHistoryInstance;
import com.ale.venus.workflow.query.HistoryInstanceQuery;
import com.ale.venus.workflow.query.mybatis.MybatisPlusHistoryInstanceQuery;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import java.util.List;

/**
 * 基于MybatisPlus的FlowHistoryInstanceDao实现
 *
 * @author Ale
 * @version 1.0.0
 */
public class MybatisPlusFlowHistoryInstanceDao implements FlowHistoryInstanceDao {

    /**
     * 历史流程实例Mapper
     */
    private final FlowHistoryInstanceMapper historyInstanceMapper;

    public MybatisPlusFlowHistoryInstanceDao(FlowHistoryInstanceMapper historyInstanceMapper) {
        this.historyInstanceMapper = historyInstanceMapper;
    }


    @Override
    public HistoryInstanceQuery createHistoryInstanceQuery() {
        return new MybatisPlusHistoryInstanceQuery(this.historyInstanceMapper);
    }

    @Override
    public FlowHistoryInstance selectById(String id) {
        return this.historyInstanceMapper.selectById(id);
    }

    @Override
    public List<FlowHistoryInstance> selectByBusinessId(String businessId) {
        return this.historyInstanceMapper.selectList(
            Wrappers.<FlowHistoryInstance>lambdaQuery()
                .eq(FlowHistoryInstance::getDeleted, false)
                .eq(FlowHistoryInstance::getBusinessId, businessId)
        );
    }

    @Override
    public List<FlowHistoryInstance> selectByBusinessType(String businessType) {
        return this.historyInstanceMapper.selectList(
            Wrappers.<FlowHistoryInstance>lambdaQuery()
                .eq(FlowHistoryInstance::getDeleted, false)
                .eq(FlowHistoryInstance::getBusinessType, businessType)
        );
    }

    @Override
    public boolean insert(FlowHistoryInstance historyInstance) {
        return this.historyInstanceMapper.insert(historyInstance) > 0;
    }

    @Override
    public boolean updateById(FlowHistoryInstance historyInstance) {
        return this.historyInstanceMapper.updateById(historyInstance) > 0;
    }
}
