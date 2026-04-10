package com.ale.venus.workflow.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.transaction.TransactionSupport;
import com.ale.venus.common.utils.JsonUtils;
import com.ale.venus.workflow.dao.FlowDefinitionDao;
import com.ale.venus.workflow.dao.FlowHistoryInstanceDao;
import com.ale.venus.workflow.dao.FlowInstanceDao;
import com.ale.venus.workflow.entity.FlowDefinition;
import com.ale.venus.workflow.entity.FlowHistoryInstance;
import com.ale.venus.workflow.entity.FlowInstance;
import com.ale.venus.workflow.enumeration.FlowDefinitionState;
import com.ale.venus.workflow.enumeration.FlowInstanceState;
import com.ale.venus.workflow.exception.FlowException;
import com.ale.venus.workflow.hook.InstanceEventPublisher;
import com.ale.venus.workflow.hook.InstanceEventType;
import com.ale.venus.workflow.model.InstanceModel;
import com.ale.venus.workflow.model.InstanceModelSupport;
import com.ale.venus.workflow.model.node.FlowNode;
import com.ale.venus.workflow.service.InstanceService;
import com.ale.venus.workflow.support.FlowContext;
import com.ale.venus.workflow.support.StartInstanceParam;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 默认流程实例服务
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
public class DefaultInstanceService implements InstanceService {

    /**
     * 流程定义dao
     */
    private final FlowDefinitionDao definitionDao;

    /**
     * 流程实例dao
     */
    private final FlowInstanceDao instanceDao;

    /**
     * 历史流程实例dao
     */
    private final FlowHistoryInstanceDao historyInstanceDao;

    /**
     * 标识当前是否处于“流程实例启动生命周期”中
     */
    private final ThreadLocal<Boolean> isStartInstanceLifecycle = ThreadLocal.withInitial(() -> false);

    /**
     * 事件延迟发布队列（线程隔离，避免并发问题）
     * 为避免在实例启动过程中触发结束导致先发布结束事件，因此使用延迟发布形式
     */
    private final ThreadLocal<List<Runnable>> eventRunnable = ThreadLocal.withInitial(ArrayList::new);

    public DefaultInstanceService(FlowDefinitionDao definitionDao, FlowInstanceDao instanceDao, FlowHistoryInstanceDao historyInstanceDao) {
        this.definitionDao = definitionDao;
        this.instanceDao = instanceDao;
        this.historyInstanceDao = historyInstanceDao;
    }

    @Override
    public FlowInstance startInstance(StartInstanceParam startInstanceParam) {
        FlowDefinition flowDefinition = Optional.ofNullable(this.definitionDao.selectById(startInstanceParam.getDefinitionId()))
            .orElseThrow(() -> new FlowException("创建流程实例失败！流程定义[{}]不存在！", startInstanceParam.getDefinitionId()));

        FlowInstance instance = FlowInstance.of(
            startInstanceParam.getBusinessId(),
            startInstanceParam.getBusinessType(),
            startInstanceParam.getVariable(),
            startInstanceParam.getStarterId()
        );
        instance.setDefinitionId(flowDefinition.getId());
        instance.setTenantId(flowDefinition.getTenantId());
        instance.setDesignContent(flowDefinition.getDesignContent());
        instance.setTitle(StrUtil.isNotBlank(startInstanceParam.getTitle()) ? startInstanceParam.getTitle() : flowDefinition.getName());
        instance.setDescription(startInstanceParam.getDescription());

        InstanceModel instanceModel = InstanceModelSupport.parseInstanceModel(instance);

        FlowNode rootNode = instanceModel.getRootNode();

        instance.setCurrentNodeId(rootNode.getId());

        return this.startInstance(instance, rootNode);
    }

    @Override
    public FlowInstance restartInstance(String instanceId, Map<String, Object> variable, String starterId) {
        // 获取原实例
        FlowHistoryInstance historyInstance = this.historyInstanceDao.selectById(instanceId);
        if (historyInstance == null) {
            throw new FlowException("重启流程实例失败！流程实例[{}]不存在！", instanceId);
        }
        Map<String, Object> historyVariable = historyInstance.getAllVariable();
        if (CollectionUtils.isNotEmpty(variable)) {
            historyVariable.putAll(variable);
        }

        return this.startInstance(
            StartInstanceParam.builder()
                .definitionId(historyInstance.getDefinitionId())
                .businessId(historyInstance.getBusinessId())
                .businessType(historyInstance.getBusinessType())
                .title(historyInstance.getTitle())
                .description(historyInstance.getDescription())
                .variable(historyVariable)
                .starterId(starterId)
                .build()
        );
    }

    @Override
    public boolean updateInstance(FlowInstance flowInstance) {
        return TransactionSupport.execute(() -> {
            InstanceEventPublisher.publishInstanceEvent(InstanceEventType.BEFORE_UPDATE, flowInstance);

            boolean executeResult = BooleanUtil.and(
                this.instanceDao.updateById(flowInstance),
                this.historyInstanceDao.updateById(FlowHistoryInstance.of(flowInstance, FlowInstanceState.ACTIVE))
            );

            if (!executeResult) {
                throw new FlowException("更新流程实例[{}]失败！", flowInstance.getId());
            }
            InstanceEventPublisher.publishInstanceEvent(InstanceEventType.AFTER_UPDATE, flowInstance);
            InstanceModelSupport.invalidationCache(flowInstance.getId());
            return true;
        });
    }

    @Override
    public boolean addVariable(String instanceId, Map<String, Object> variable) {
        FlowInstance instance = this.fetchInstanceAndCheckExists(instanceId);
        instance.addVariable(variable);
        return this.updateInstance(instance);
    }

    @Override
    public boolean appendNode(String instanceId, FlowNode flowNode) {
        FlowInstance instance = this.fetchInstanceAndCheckExists(instanceId);
        InstanceModel instanceModel = InstanceModelSupport.parseInstanceModel(instance);
        FlowNode rootNode = instanceModel.getRootNode();
        FlowNode parentNode = instanceModel.findNode(flowNode.getParentId());
        if (parentNode == null) {
            throw new FlowException("添加节点失败！未找到插入节点！");
        }
        FlowNode child = parentNode.getChild();
        flowNode.setChild(child);
        child.setParentId(flowNode.getId());
        parentNode.setChild(flowNode);
        instance.setDesignContent(JsonUtils.toJson(rootNode));
        return this.updateInstance(instance);
    }

    @Override
    public boolean finish(String instanceId, boolean isAuto) {
        return this.endInstance(
            instanceId,
            isAuto ? FlowInstanceState.AUTO_COMPLETE : FlowInstanceState.COMPLETE,
            InstanceEventType.BEFORE_COMPLETE,
            InstanceEventType.AFTER_COMPLETE,
            false
        );
    }

    @Override
    public boolean reject(String instanceId, boolean isAuto) {
        FlowInstance flowInstance = this.fetchInstanceAndCheckExists(instanceId);
        return this.endInstance(
            flowInstance,
            isAuto ? FlowInstanceState.AUTO_REJECT : FlowInstanceState.REJECT,
            InstanceEventType.BEFORE_REJECT,
            InstanceEventType.AFTER_REJECT,
            true
        );
    }

    @Override
    public boolean revoke(String instanceId) {
        return this.endInstance(
            instanceId,
            FlowInstanceState.REVOKE,
            InstanceEventType.BEFORE_REVOKE,
            InstanceEventType.AFTER_REVOKE,
            true
        );
    }

    @Override
    public boolean timeout(String instanceId) {
        return this.endInstance(
            instanceId,
            FlowInstanceState.TIMEOUT,
            InstanceEventType.BEFORE_TIMEOUT,
            InstanceEventType.AFTER_TIMEOUT,
            true
        );
    }

    @Override
    public void checkInstanceAllowExecution(String instanceId) {
        FlowInstance flowInstance = this.instanceDao.selectById(instanceId);
        if (flowInstance == null) {
            throw new FlowException("流程实例[{}]不存在！", instanceId);
        }
        FlowDefinition flowDefinition = this.definitionDao.selectById(flowInstance.getDefinitionId());
        if (flowDefinition == null || BooleanUtil.isTrue(flowDefinition.getDeleted())) {
            throw new FlowException("流程定义[{}]不存在！", flowInstance.getDefinitionId());
        }
        if (Objects.equals(flowDefinition.getState(), FlowDefinitionState.SUSPENDED.getValue())) {
            throw new FlowException("流程[{}][{}]已挂起！", flowDefinition.getName(), flowInstance.getDefinitionId());
        }
    }

    /**
     * 获取流程实例并校验是否存在
     *
     * @param instanceId 流程实例ID
     * @return 流程实例
     */
    private FlowInstance fetchInstanceAndCheckExists(String instanceId) {
        FlowInstance flowInstance = this.instanceDao.selectById(instanceId);
        if (flowInstance == null) {
            throw new FlowException("流程实例[{}]不存在！", instanceId);
        }
        return flowInstance;
    }

    /**
     * 启动流程实例
     *
     * @param instance  流程实例
     * @param startNode 流程开始节点
     * @return 流程实例
     */
    private FlowInstance startInstance(FlowInstance instance, FlowNode startNode) {
        FlowHistoryInstance historyInstance = FlowHistoryInstance.of(instance, FlowInstanceState.ACTIVE);

        TransactionSupport.execute(() -> {
            this.isStartInstanceLifecycle.set(true);
            InstanceEventPublisher.publishInstanceEvent(InstanceEventType.BEFORE_START, instance);
            boolean executeResult = BooleanUtil.and(
                this.instanceDao.insert(instance),
                this.historyInstanceDao.insert(historyInstance),
                // 执行开始节点
                startNode.execute(instance, null, null)
            );
            if (!executeResult) {
                throw new FlowException("创建流程实例失败！流程定义：{}，业务ID：{}，业务类型{}", instance.getDefinitionId(), instance.getBusinessId(), instance.getBusinessType());
            }
            InstanceEventPublisher.publishInstanceEvent(InstanceEventType.AFTER_START, instance);
            this.isStartInstanceLifecycle.set(false);
            List<Runnable> runnableList = eventRunnable.get();
            if (CollectionUtils.isNotEmpty(runnableList)) {
                runnableList.forEach(Runnable::run);
                runnableList.clear();
            }
            return true;
        });

        // 清理 ThreadLocal，避免内存泄漏
        this.isStartInstanceLifecycle.remove();
        this.eventRunnable.remove();

        return instance;
    }

    /**
     * 结束流程实例
     *
     * @param instanceId      流程实例ID
     * @param state           流程实例状态
     * @param beforeEventType 前置事件类型
     * @param afterEventType  后置事件类型
     * @param isForce         是否为强制结束
     * @return 结果
     */
    private boolean endInstance(String instanceId, FlowInstanceState state, InstanceEventType beforeEventType, InstanceEventType afterEventType, boolean isForce) {
        return this.endInstance(this.fetchInstanceAndCheckExists(instanceId), state, beforeEventType, afterEventType, isForce);
    }

    /**
     * 结束流程实例
     *
     * @param instance        流程实例
     * @param state           流程实例状态
     * @param beforeEventType 前置事件类型
     * @param afterEventType  后置事件类型
     * @param isForce         是否为强制结束
     * @return 结果
     */
    private boolean endInstance(FlowInstance instance, FlowInstanceState state, InstanceEventType beforeEventType, InstanceEventType afterEventType, boolean isForce) {
        InstanceModelSupport.invalidationCache(instance.getId());
        return TransactionSupport.execute(() -> {
            if (this.isStartInstanceLifecycle.get()) {
                this.eventRunnable.get().add(() -> InstanceEventPublisher.publishInstanceEvent(beforeEventType, instance));
            } else {
                InstanceEventPublisher.publishInstanceEvent(beforeEventType, instance);
            }
            FlowHistoryInstance historyInstance = FlowHistoryInstance.of(instance, state);
            if (isForce) {
                boolean result = FlowContext.getExecutionService().forceFinishExecution(instance.getId(), state);
                if (!result) {
                    throw new FlowException("强制结束流程实例[{}]失败！", instance.getId());
                }
            }
            if (this.isStartInstanceLifecycle.get()) {
                this.eventRunnable.get().add(() -> InstanceEventPublisher.publishInstanceEvent(afterEventType, historyInstance));
            } else {
                InstanceEventPublisher.publishInstanceEvent(afterEventType, historyInstance);
            }
            if (log.isDebugEnabled()) {
                log.debug("流程实例[{}][{}]成功！", instance.getId(), state.getName());
            }
            return this.instanceDao.deleteById(instance.getId()) && this.historyInstanceDao.updateById(historyInstance);
        });
    }
}
