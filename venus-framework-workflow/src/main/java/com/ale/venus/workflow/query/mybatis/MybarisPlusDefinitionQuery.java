package com.ale.venus.workflow.query.mybatis;

import cn.hutool.core.util.StrUtil;
import com.ale.venus.workflow.dao.mybatis.mapper.FlowDefinitionMapper;
import com.ale.venus.workflow.entity.FlowDefinition;
import com.ale.venus.workflow.enumeration.FlowDefinitionState;
import com.ale.venus.workflow.query.DefinitionQuery;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;


/**
 * 基于MybatisPlus的流程定义查询构建器
 *
 * @author Ale
 * @version 1.0.0
 */
public class MybarisPlusDefinitionQuery extends AbstractMybatisPlusBaseQuery<FlowDefinition, DefinitionQuery> implements DefinitionQuery {

    /**
     * 流程定义Mapper
     */
    private final FlowDefinitionMapper definitionMapper;

    public MybarisPlusDefinitionQuery(FlowDefinitionMapper definitionMapper) {
        this.definitionMapper = definitionMapper;
    }

    /**
     * 流程定义Key
     */
    private String definitionKey;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 流程定义名称
     */
    private String name;

    /**
     * 是否发布
     */
    private Boolean published;

    /**
     * 状态
     */
    private FlowDefinitionState state;

    @Override
    public DefinitionQuery definitionKey(String definitionKey) {
        this.definitionKey = definitionKey;
        return this;
    }

    @Override
    public DefinitionQuery businessType(String businessType) {
        this.businessType = businessType;
        return this;
    }

    @Override
    public DefinitionQuery nameLike(String name) {
        this.name = name;
        return this;
    }

    @Override
    public DefinitionQuery published(Boolean published) {
        this.published = published;
        return this;
    }

    @Override
    public DefinitionQuery state(FlowDefinitionState state) {
        this.state = state;
        return this;
    }

    @Override
    protected void executeBuildWrapper(QueryWrapper<FlowDefinition> queryWrapper) {
        super.executeBuildWrapper(queryWrapper);
        queryWrapper.eq(StrUtil.toUnderlineCase(FlowDefinition.Fields.deleted), false)
            .eq(StrUtil.isNotBlank(this.definitionKey), StrUtil.toUnderlineCase(FlowDefinition.Fields.definitionKey), this.definitionKey)
            .eq(StrUtil.isNotBlank(this.businessType), StrUtil.toUnderlineCase(FlowDefinition.Fields.businessType), this.businessType)
            .like(StrUtil.isNotBlank(this.name), StrUtil.toUnderlineCase(FlowDefinition.Fields.name), this.name)
            .eq(this.published != null, StrUtil.toUnderlineCase(FlowDefinition.Fields.published), this.published);
        if (this.state != null) {
            queryWrapper.eq(StrUtil.toUnderlineCase(FlowDefinition.Fields.state), this.state.getValue());
        }
    }

    @Override
    protected BaseMapper<FlowDefinition> provideMapper() {
        return this.definitionMapper;
    }
}
