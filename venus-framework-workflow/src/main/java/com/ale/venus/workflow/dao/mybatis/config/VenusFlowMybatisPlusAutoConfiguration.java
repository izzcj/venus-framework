package com.ale.venus.workflow.dao.mybatis.config;

import com.ale.venus.workflow.dao.*;
import com.ale.venus.workflow.dao.mybatis.*;
import com.ale.venus.workflow.dao.mybatis.mapper.*;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Vef流程引擎MybatisPlus配置类
 *
 * @author Ale
 * @version 1.0.0
 */
@AutoConfiguration
@MapperScan("com.ale.venus.workflow.dao.mybatis.mapper")
public class VenusFlowMybatisPlusAutoConfiguration {

    /**
     * 流程定义Dao
     *
     * @param flowDefinitionMapper 流程定义Mapper
     * @return 流程定义DaoBean
     */
    @Bean
    @ConditionalOnMissingBean
    public FlowDefinitionDao flowDefinitionDao(FlowDefinitionMapper flowDefinitionMapper) {
        return new MybatisPlusFlowDefinitionDao(flowDefinitionMapper);
    }

    /**
     * 流程实例Dao
     *
     * @param flowInstanceMapper 流程实例Mapper
     * @return 流程实例DaoBean
     */
    @Bean
    @ConditionalOnMissingBean
    public FlowInstanceDao flowInstanceDao(FlowInstanceMapper flowInstanceMapper) {
        return new MybatisPlusFlowInstanceDao(flowInstanceMapper);
    }

    /**
     * 历史流程实例Dao
     *
     * @param flowHistoryInstanceMapper 历史流程实例Mapper
     * @return 历史流程实例DaoBean
     */
    @Bean
    @ConditionalOnMissingBean
    public FlowHistoryInstanceDao flowHistoryInstanceDao(FlowHistoryInstanceMapper flowHistoryInstanceMapper) {
        return new MybatisPlusFlowHistoryInstanceDao(flowHistoryInstanceMapper);
    }

    /**
     * 流程执行记录Dao
     *
     * @param flowExecutionMapper 流程执行记录Mapper
     * @return 流程任务DaoBean
     */
    @Bean
    @ConditionalOnMissingBean
    public FlowExecutionDao flowExecutionDao(FlowExecutionMapper flowExecutionMapper) {
        return new MybatisPlusFlowExecutionDao(flowExecutionMapper);
    }

    /**
     * 流程任务Dao
     *
     * @param flowTaskMapper 流程任务Mapper
     * @return 流程任务DaoBean
     */
    @Bean
    @ConditionalOnMissingBean
    public FlowTaskDao flowTaskDao(FlowTaskMapper flowTaskMapper) {
        return new MybatisPlusFlowTaskDao(flowTaskMapper);
    }

    /**
     * 历史流程任务Dao
     *
     * @param flowHistoryTaskMapper 历史流程任务Mapper
     * @return 历史流程任务DaoBean
     */
    @Bean
    @ConditionalOnMissingBean
    public FlowHistoryTaskDao flowHistoryTaskDao(FlowHistoryTaskMapper flowHistoryTaskMapper) {
        return new MybatisPlusFlowHistoryTaskDao(flowHistoryTaskMapper);
    }

    /**
     * 流程任务参与人dao
     *
     * @param flowTaskActorMapper 流程任务参与人Mapper
     * @return 流程任务参与人DaoBean
     */
    @Bean
    @ConditionalOnMissingBean
    public FlowTaskActorDao flowTaskActorDao(FlowTaskActorMapper flowTaskActorMapper) {
        return new MybatisPlusFlowTaskActorDao(flowTaskActorMapper);
    }

    /**
     * 历史流程任务参与人dao
     *
     * @param flowHistoryTaskActorMapper 历史流程任务参与人Mapper
     * @return 历史流程任务参与人DaoBean
     */
    @Bean
    @ConditionalOnMissingBean
    public FlowHistoryTaskActorDao flowHistoryTaskActorDao(FlowHistoryTaskActorMapper flowHistoryTaskActorMapper) {
        return new MybatisPlusFlowHistoryTaskActorDao(flowHistoryTaskActorMapper);
    }
}
