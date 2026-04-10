package com.ale.venus.workflow.service.impl;

import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.support.Option;
import com.ale.venus.workflow.dao.*;
import com.ale.venus.workflow.entity.*;
import com.ale.venus.workflow.enumeration.FlowExecutionState;
import com.ale.venus.workflow.enumeration.FlowTaskType;
import com.ale.venus.workflow.enumeration.NodeExecuteState;
import com.ale.venus.workflow.model.InstanceModel;
import com.ale.venus.workflow.model.InstanceModelSupport;
import com.ale.venus.workflow.model.node.BranchNode;
import com.ale.venus.workflow.model.node.FlowNode;
import com.ale.venus.workflow.query.*;
import com.ale.venus.workflow.service.QueryService;
import com.ale.venus.workflow.support.InstanceExecutionInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Ale
 * @version 1.0.0
 */
public class DefaultQueryService implements QueryService {

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
     * 流程执行dao
     */
    private final FlowExecutionDao executionDao;

    /**
     * 流程任务dao
     */
    private final FlowTaskDao taskDao;

    /**
     * 流程任务参与人dao
     */
    private final FlowTaskActorDao taskActorDao;

    /**
     * 历史流程任务dao
     */
    private final FlowHistoryTaskDao historyTaskDao;

    /**
     * 历史流程任务参与人dao
     */
    private final FlowHistoryTaskActorDao historyTaskActorDao;

    public DefaultQueryService(FlowDefinitionDao definitionDao,
                               FlowInstanceDao instanceDao,
                               FlowHistoryInstanceDao historyInstanceDao,
                               FlowExecutionDao executionDao,
                               FlowTaskDao taskDao,
                               FlowTaskActorDao taskActorDao,
                               FlowHistoryTaskDao historyTaskDao,
                               FlowHistoryTaskActorDao historyTaskActorDao) {
        this.definitionDao = definitionDao;
        this.instanceDao = instanceDao;
        this.historyInstanceDao = historyInstanceDao;
        this.executionDao = executionDao;
        this.taskDao = taskDao;
        this.taskActorDao = taskActorDao;
        this.historyTaskDao = historyTaskDao;
        this.historyTaskActorDao = historyTaskActorDao;
    }

    @Override
    public DefinitionQuery createDefinitionQuery() {
        return this.definitionDao.createDefinitionQuery();
    }

    @Override
    public FlowDefinition fetchDefinitionById(String id) {
        return this.definitionDao.selectById(id);
    }

    @Override
    public FlowDefinition fetchDefinitionByKey(String key, String tenantId) {
        return this.definitionDao.selectByKey(key, tenantId);
    }

    @Override
    public List<FlowDefinition> fetchDefinitionByType(String type, String tenantId) {
        return this.definitionDao.selectListByBusinessType(type, tenantId);
    }

    @Override
    public ActiveInstanceQuery createActiveInstanceQuery() {
        return this.instanceDao.createActiveInstanceQuery();
    }

    @Override
    public FlowInstance fetchInstanceById(String id) {
        return this.instanceDao.selectById(id);
    }

    @Override
    public FlowInstance fetchInstanceByBusinessIdAndType(String businessId, String businessType) {
        return this.instanceDao.selectByBusinessIdAndType(businessId, businessType);
    }

    @Override
    public HistoryInstanceQuery createHistoryInstanceQuery() {
        return this.historyInstanceDao.createHistoryInstanceQuery();
    }

    @Override
    public List<FlowHistoryInstance> fetchHistoryInstanceByBusinessId(String businessId) {
        return this.historyInstanceDao.selectByBusinessId(businessId);
    }

    @Override
    public List<FlowHistoryInstance> fetchHistoryInstanceByBusinessType(String businessType) {
        return this.historyInstanceDao.selectByBusinessType(businessType);
    }

    @Override
    public ActiveTaskQuery createActiveTaskQuery() {
        return this.taskDao.createActiveTaskQuery();
    }

    @Override
    public FlowTask fetchTaskById(String id) {
        return this.taskDao.selectById(id);
    }

    @Override
    public HistoryTaskQuery createHistoryTaskQuery() {
        return this.historyTaskDao.createHistoryTaskQuery();
    }

    @Override
    public FlowHistoryTask fetchHistoryTaskById(String id) {
        return this.historyTaskDao.selectById(id);
    }

    @Override
    public FlowNode fetchNodeByInstanceId(String instanceId) {
        Set<String> executingNodeIds = this.taskDao.selectByInstanceId(instanceId)
            .stream()
            .map(FlowTask::getNodeId)
            .collect(Collectors.toSet());
        Set<String> executedNodeIds = this.executionDao.selectByInstanceIdAndState(instanceId)
            .stream()
            .peek(execution -> {
                if (FlowExecutionState.ACTIVE.match(execution.getState())) {
                    executingNodeIds.add(execution.getNodeId());
                }
            })
            .filter(execution -> FlowExecutionState.COMPLETE.match(execution.getState()))
            .map(FlowExecution::getNodeId)
            .collect(Collectors.toSet());
        FlowHistoryInstance historyInstance = this.historyInstanceDao.selectById(instanceId);
        InstanceModel instanceModel = InstanceModelSupport.parseInstanceModel(historyInstance, false);
        FlowNode rootNode = instanceModel.getRootNode();
        Queue<FlowNode> queue = Lists.newLinkedList();
        queue.offer(rootNode);
        while (!queue.isEmpty()) {
            FlowNode node = queue.poll();
            if (node instanceof BranchNode branchNode) {
                queue.addAll(branchNode.getBranch());
            }
            if (node.getChild() != null) {
                queue.offer(node.getChild());
            }
            if (executedNodeIds.contains(node.getId())) {
                node.setExecuteState(NodeExecuteState.EXECUTED);
                continue;
            }
            if (executingNodeIds.contains(node.getId())) {
                node.setExecuteState(NodeExecuteState.EXECUTING);
            }
        }
        return rootNode;
    }

    @Override
    public InstanceExecutionInfo fetchInstanceExecutionInfo(String instanceId) {
        FlowHistoryInstance instance = this.historyInstanceDao.selectById(instanceId);
        List<FlowExecution> executions = this.executionDao.selectByInstanceIdAndState(instanceId);
        List<FlowTask> tasks = this.taskDao.selectByInstanceId(instanceId);
        List<FlowTaskActor> taskActors = this.taskActorDao.selectByInstanceId(instanceId);
        List<FlowHistoryTask> historyTasks = this.historyTaskDao.selectByInstanceId(instanceId);
        List<FlowHistoryTaskActor> historyTaskActors = this.historyTaskActorDao.selectByInstanceId(instanceId);
        return InstanceExecutionInfo.of(instance, executions, tasks, taskActors, historyTasks, historyTaskActors);
    }

    @Override
    public List<Option> fetchRollbackNodeOption(String taskId) {
        FlowTask task = this.taskDao.selectById(taskId);
        List<FlowExecution> completedExecutions = this.executionDao.selectByInstanceIdAndState(task.getInstanceId(), FlowExecutionState.COMPLETE);
        InstanceModel instanceModel = InstanceModelSupport.parseInstanceModel(task.getInstanceId());
        FlowNode taskNode = instanceModel.findNode(task.getNodeId());
        Set<String> existsNodeIds = Sets.newHashSet();
        return completedExecutions.stream()
            .filter(execution -> FlowTaskType.APPROVAL.match(execution.getNodeType()))
            .filter(execution -> {
                FlowNode executionNode = instanceModel.findNode(execution.getNodeId());
                // 回退节点不能为当前节点
                if (Objects.equals(executionNode.getId(), taskNode.getId())) {
                    return false;
                }
                // 同级节点
                return StrUtil.isBlank(executionNode.getBranchId()) && StrUtil.isBlank(taskNode.getBranchId())
                    || Objects.equals(executionNode.getBranchId(), taskNode.getBranchId());
            })
            .filter(execution -> {
                if (existsNodeIds.contains(execution.getNodeId())) {
                    return false;
                }
                existsNodeIds.add(execution.getNodeId());
                return true;
            })
            .map(execution -> Option.of(execution.getNodeName(), execution.getNodeId()))
            .toList();
    }
}
