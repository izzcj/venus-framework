package com.ale.venus.workflow.dao.mybatis;

import cn.hutool.core.util.StrUtil;
import com.ale.venus.workflow.dao.FlowDefinitionDao;
import com.ale.venus.workflow.dao.mybatis.mapper.FlowDefinitionMapper;
import com.ale.venus.workflow.entity.FlowDefinition;
import com.ale.venus.workflow.query.DefinitionQuery;
import com.ale.venus.workflow.query.mybatis.MybarisPlusDefinitionQuery;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import java.util.List;

/**
 * 基于MybatisPlus的FlowDefinitionDao实现
 *
 * @author Ale
 * @version 1.0.0
 */
public class MybatisPlusFlowDefinitionDao implements FlowDefinitionDao {

    /**
     * 映射器
     */
    private final FlowDefinitionMapper definitionMapper;

    public MybatisPlusFlowDefinitionDao(FlowDefinitionMapper definitionMapper) {
        this.definitionMapper = definitionMapper;
    }

    @Override
    public DefinitionQuery createDefinitionQuery() {
        return new MybarisPlusDefinitionQuery(this.definitionMapper);
    }

    @Override
    public FlowDefinition selectById(String id) {
        return this.definitionMapper.selectById(id);
    }

    @Override
    public FlowDefinition selectByKey(String key, String tenantId) {
        return this.definitionMapper.selectOne(
            Wrappers.<FlowDefinition>lambdaQuery()
                .eq(FlowDefinition::getDeleted, false)
                .eq(StrUtil.isNotBlank(tenantId), FlowDefinition::getTenantId, tenantId)
                .eq(FlowDefinition::getDefinitionKey, key),
            false
        );
    }

    @Override
    public List<FlowDefinition> selectListByBusinessType(String type, String tenantId) {
        return this.definitionMapper.selectList(
            Wrappers.<FlowDefinition>lambdaQuery()
                .eq(FlowDefinition::getDeleted, false)
                .eq(StrUtil.isNotBlank(tenantId), FlowDefinition::getTenantId, tenantId)
                .eq(FlowDefinition::getBusinessType, type)
        );
    }

    @Override
    public boolean insert(FlowDefinition flowDefinition) {
        return this.definitionMapper.insert(flowDefinition) > 0;
    }

    @Override
    public boolean updateById(FlowDefinition flowDefinition) {
        return this.definitionMapper.updateById(flowDefinition) > 0;
    }

    @Override
    public boolean updateByKey(FlowDefinition flowDefinition, String key, String tenantId) {
        return this.definitionMapper.update(
            flowDefinition,
            Wrappers.<FlowDefinition>lambdaQuery()
                .eq(StrUtil.isNotBlank(tenantId), FlowDefinition::getTenantId, tenantId)
                .eq(FlowDefinition::getDefinitionKey, key)
        ) > 0;
    }

    @Override
    public boolean deleteById(String id) {
        return this.definitionMapper.update(
            Wrappers.<FlowDefinition>lambdaUpdate()
                .set(FlowDefinition::getDeleted, true)
                .eq(FlowDefinition::getId, id)
        ) > 0;
    }

    @Override
    public boolean exist(String id) {
        return this.definitionMapper.exists(
            Wrappers.<FlowDefinition>lambdaQuery()
                .eq(FlowDefinition::getDeleted, false)
                .eq(FlowDefinition::getId, id)
        );
    }
}
