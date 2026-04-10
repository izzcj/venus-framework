package com.ale.venus.workflow.service.config;

import com.ale.venus.workflow.dao.*;
import com.ale.venus.workflow.service.*;
import com.ale.venus.workflow.service.impl.*;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Venus流程引擎服务自动配置类
 *
 * @author Ale
 * @version 1.0.0
 */
@AutoConfiguration
public class VenusFlowServiceAutoConfiguration {

    /**
     * 流程定义服务
     *
     * @param definitionDao 流程定义Dao
     * @return 流程定义服务bean
     */
    @Bean
    @ConditionalOnBean(FlowDefinitionDao.class)
    @ConditionalOnMissingBean
    public DefinitionService definitionService(FlowDefinitionDao definitionDao) {
        return new DefaultDefinitionService(definitionDao);
    }

    /**
     * 流程实例服务
     *
     * @param definitionDao      流程定义Dao
     * @param instanceDao        流程实例Dao
     * @param historyInstanceDao 流程历史实例Dao
     * @return 流程实例服务bean
     */
    @Bean
    @ConditionalOnBean({FlowDefinitionDao.class, FlowInstanceDao.class, FlowHistoryInstanceDao.class})
    @ConditionalOnMissingBean
    public InstanceService instanceService(FlowDefinitionDao definitionDao, FlowInstanceDao instanceDao, FlowHistoryInstanceDao historyInstanceDao) {
        return new DefaultInstanceService(definitionDao, instanceDao, historyInstanceDao);
    }

    /**
     * 流程任务服务
     *
     * @param taskDao             流程任务Dao
     * @param taskActorDao        流程任务参与者Dao
     * @param historyTaskDao      流程历史任务Dao
     * @param historyTaskActorDao 流程历史任务参与者Dao
     * @return 流程任务服务bean
     */
    @Bean
    @ConditionalOnBean({FlowTaskDao.class, FlowTaskActorDao.class, FlowHistoryTaskDao.class, FlowHistoryTaskActorDao.class})
    @ConditionalOnMissingBean
    public TaskService taskService(FlowTaskDao taskDao, FlowTaskActorDao taskActorDao, FlowHistoryTaskDao historyTaskDao, FlowHistoryTaskActorDao historyTaskActorDao) {
        return new DefaultTaskService(taskDao, taskActorDao, historyTaskDao, historyTaskActorDao);
    }

    /**
     * 流程执行服务
     *
     * @param executionDao 流程执行Dao
     * @return 流程执行服务bean
     */
    @Bean
    @ConditionalOnBean(FlowExecutionDao.class)
    @ConditionalOnMissingBean
    public ExecutionService executionService(FlowExecutionDao executionDao) {
        return new DefaultExecutionService(executionDao);
    }

    /**
     * 流程查询服务
     *
     * @param definitionDao       流程定义Dao
     * @param instanceDao         流程实例Dao
     * @param historyInstanceDao  历史流程实例Dao
     * @param executionDao        流程执行Dao
     * @param taskDao             流程任务Dao
     * @param taskActorDao        流程任务参与人Dao
     * @param historyTaskDao      历史流程任务Dao
     * @param historyTaskActorDao 历史流程任务参与人Dao
     * @return 流程查询服务bean
     */
    @Bean
    @ConditionalOnBean({
        FlowDefinitionDao.class,
        FlowInstanceDao.class,
        FlowHistoryInstanceDao.class,
        FlowExecutionDao.class,
        FlowTaskDao.class,
        FlowTaskActorDao.class,
        FlowHistoryTaskDao.class,
        FlowHistoryTaskActorDao.class
    })
    @ConditionalOnMissingBean
    public QueryService queryService(FlowDefinitionDao definitionDao,
                                     FlowInstanceDao instanceDao,
                                     FlowHistoryInstanceDao historyInstanceDao,
                                     FlowExecutionDao executionDao,
                                     FlowTaskDao taskDao,
                                     FlowTaskActorDao taskActorDao,
                                     FlowHistoryTaskDao historyTaskDao,
                                     FlowHistoryTaskActorDao historyTaskActorDao) {
        return new DefaultQueryService(definitionDao, instanceDao, historyInstanceDao, executionDao, taskDao, taskActorDao, historyTaskDao, historyTaskActorDao);
    }
}
