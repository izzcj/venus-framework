package com.ale.venus.workflow.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.transaction.TransactionSupport;
import com.ale.venus.common.utils.CastUtils;
import com.ale.venus.workflow.constants.FlowVariableConstants;
import com.ale.venus.workflow.dao.FlowExecutionDao;
import com.ale.venus.workflow.entity.FlowExecution;
import com.ale.venus.workflow.entity.FlowInstance;
import com.ale.venus.workflow.enumeration.FlowExecutionState;
import com.ale.venus.workflow.enumeration.FlowInstanceState;
import com.ale.venus.workflow.enumeration.FlowTaskType;
import com.ale.venus.workflow.exception.FlowException;
import com.ale.venus.workflow.model.InstanceModel;
import com.ale.venus.workflow.model.InstanceModelSupport;
import com.ale.venus.workflow.model.TaskAssignee;
import com.ale.venus.workflow.model.node.FlowNode;
import com.ale.venus.workflow.service.ExecutionService;
import com.ale.venus.workflow.support.FlowContext;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 默认流程执行记录服务
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
public class DefaultExecutionService implements ExecutionService {

    /**
     * 流程执行记录dao
     */
    private final FlowExecutionDao executionDao;

    public DefaultExecutionService(FlowExecutionDao executionDao) {
        this.executionDao = executionDao;
    }

    @Override
    public FlowExecution fetchExecution(String executionId) {
        return this.executionDao.selectById(executionId);
    }

    @Override
    public FlowExecution createExecution(FlowInstance instance, String nodeId, FlowExecution lastExecution, Map<String, Object> variable) {
        InstanceModel instanceModel = InstanceModelSupport.parseInstanceModel(instance);

        // 更新实例执行节点信息
        FlowNode currentNode = instanceModel.findNode(nodeId);
        instance.setCurrentNodeId(currentNode.getId());
        FlowContext.getInstanceService().updateInstance(instance);

        String lastExecutionId = null;
        String parentId = null;
        if (lastExecution != null) {
            lastExecutionId = lastExecution.getId();
            // 上次执行节点为分支节点时，父级ID为执行记录的ID，如果当前节点为分支节点的分支则父级ID为执行记录的父级ID
            if (FlowTaskType.PARALLEL.match(lastExecution.getNodeType()) || FlowTaskType.EXCLUSIVE.match(lastExecution.getNodeType())) {
                parentId = lastExecution.getId();
            } else if (StrUtil.isNotBlank(currentNode.getBranchId())) {
                parentId = lastExecution.getParentId();
            }
        }
        FlowExecution execution = FlowExecution.of(instance, currentNode, lastExecutionId, parentId, variable);
        boolean insertResult = this.executionDao.insert(execution);
        if (!insertResult) {
            throw new FlowException("创建流程执行记录失败！");
        }
        return execution;
    }

    @Override
    public boolean forceFinishExecution(String instanceId, FlowInstanceState instanceState) {
        List<FlowExecution> executions = this.executionDao.selectByInstanceIdAndState(instanceId, FlowExecutionState.ACTIVE, FlowExecutionState.SUSPEND);
        return TransactionSupport.execute(() -> {
            boolean executeResult = true;
            for (FlowExecution execution : executions) {
                executeResult = this.forceFinishExecution(execution, instanceState);
            }
            return executeResult;
        });
    }

    @Override
    public boolean suspendExecution(String instanceId) {
        List<FlowExecution> activeExecutions = this.executionDao.selectByInstanceIdAndState(instanceId, FlowExecutionState.ACTIVE);
        return TransactionSupport.execute(() -> {
            boolean executeResult = true;
            for (FlowExecution activeExecution : activeExecutions) {
                activeExecution.setState(FlowExecutionState.SUSPEND.getValue());
                executeResult = this.executionDao.updateById(activeExecution) && FlowContext.getTaskService().suspendTask(activeExecution.getId());
            }
            return executeResult;
        });
    }

    @Override
    public boolean activeExecution(String instanceId) {
        List<FlowExecution> suspendExecutions = this.executionDao.selectByInstanceIdAndState(instanceId, FlowExecutionState.SUSPEND);
        return TransactionSupport.execute(() -> {
            boolean executeResult = true;
            for (FlowExecution suspendExecution : suspendExecutions) {
                suspendExecution.setState(FlowExecutionState.ACTIVE.getValue());
                executeResult = this.executionDao.updateById(suspendExecution) && FlowContext.getTaskService().activeTask(suspendExecution.getId());
            }
            return executeResult;
        });
    }

    @Override
    public boolean addVariable(String executionId, Map<String, Object> variable) {
        FlowExecution execution = this.executionDao.selectById(executionId);
        if (execution == null) {
            throw new FlowException("添加变量失败，执行记录[{}]不存在！", executionId);
        }
        execution.addVariable(variable);
        return this.updateExecution(execution);
    }

    @Override
    public boolean updateExecution(FlowExecution execution) {
        return this.executionDao.updateById(execution);
    }

    @Override
    public boolean completeExecution(FlowExecution execution) {
        return this.completeExecution(execution, null);
    }

    @Override
    public boolean completeExecution(FlowExecution execution, FlowExecution lastExecution) {
        execution.setState(FlowExecutionState.COMPLETE.getValue());
        execution.setCompletedTime(LocalDateTime.now());
        this.executionDao.updateById(execution);

        InstanceModel instanceModel = InstanceModelSupport.parseInstanceModel(execution.getInstanceId());
        FlowInstance instance = instanceModel.getFlowInstance();
        FlowNode currentNode = instanceModel.findNode(execution.getNodeId());
        FlowNode nextNode;

        Map<String, Object> variable = Maps.newHashMap();
        String jumpExecutionId = CastUtils.cast(execution.getVariableByKey(FlowVariableConstants.JUMP_EXECUTION_ID));
        if (StrUtil.isNotBlank(jumpExecutionId)) {
            FlowExecution jumpExecution = this.executionDao.selectById(jumpExecutionId);
            // 避免从分支回退至顶级节点后parentId丢失
            if (lastExecution == null) {
                execution.setParentId(jumpExecution.getParentId());
            }
            nextNode = instanceModel.findNode(jumpExecution.getNodeId());
            String nextJumpExecutionId = CastUtils.cast(jumpExecution.getVariableByKey(FlowVariableConstants.JUMP_EXECUTION_ID));
            if (StrUtil.isBlank(nextJumpExecutionId)) {
                this.activeExecution(jumpExecution.getInstanceId());
            } else {
                // 存在多级跳转需要传递跳转ID
                variable.put(FlowVariableConstants.JUMP_EXECUTION_ID, nextJumpExecutionId);
            }
        } else {
            nextNode = currentNode.getChild();
        }

        if (nextNode != null) {
            return nextNode.execute(instanceModel.getFlowInstance(), lastExecution == null ? execution : lastExecution, variable);
        }

        // 不为分支则结束
        if (StrUtil.isBlank(currentNode.getBranchId())) {
            boolean finished = FlowContext.getInstanceService().finish(execution.getInstanceId());
            if (!finished) {
                log.error("结束流程实例失败！流程实例ID：{}", execution.getInstanceId());
            }
            return finished;
        }

        this.handleBatchExecution(instance, execution, currentNode);

        return true;
    }

    @Override
    public TaskAssignee getNextTaskAssignee(String executionId) {
        FlowExecution execution = this.executionDao.selectById(executionId);
        if (execution == null) {
            throw new FlowException("获取下一个受理人失败！流程执行记录[{}]不存在！", executionId);
        }
        Object taskAssignees = execution.getVariableByKey(FlowVariableConstants.TASK_ASSIGNEES);
        if (taskAssignees == null) {
            return null;
        }
        TaskAssignee result = null;
        if (taskAssignees instanceof List<?> taskAssigneesList) {
            if (CollectionUtil.isEmpty(taskAssigneesList)) {
                return null;
            }
            Object nextAssignee = taskAssigneesList.getFirst();
            if (nextAssignee instanceof TaskAssignee taskAssignee) {
                result = taskAssignee;
            } else if (nextAssignee instanceof Map<?, ?> taskAssigneeMap) {
                result = BeanUtil.fillBeanWithMap(taskAssigneeMap, TaskAssignee.builder().build(), true);
            }
            taskAssigneesList.removeFirst();
            execution.addVariable(FlowVariableConstants.TASK_ASSIGNEES, taskAssigneesList);
            this.executionDao.updateById(execution);
        }
        return result;
    }

    /**
     * 处理分支执行
     *
     * @param instance    流程实例
     * @param execution   流程执行
     * @param currentNode 当前节点
     */
    private void handleBatchExecution(FlowInstance instance, FlowExecution execution, FlowNode currentNode) {
        // 为分支则找到分支节点的执行记录
        FlowExecution branchNodeExecution = this.executionDao.selectById(execution.getParentId());
        if (branchNodeExecution == null) {
            log.error("获取分支节点执行记录失败！分支节点ID：{}", currentNode.getBranchId());
        } else {
            int completeBranchCount = CastUtils.cast(
                Optional.ofNullable(branchNodeExecution.getVariableByKey(FlowVariableConstants.COMPLETE_BRANCH_COUNT))
                    .orElse(0)
            );
            completeBranchCount++;
            branchNodeExecution.addVariable(FlowVariableConstants.COMPLETE_BRANCH_COUNT, completeBranchCount);
            int passBranchCount = CastUtils.cast(
                Optional.ofNullable(branchNodeExecution.getVariableByKey(FlowVariableConstants.PASS_BRANCH_COUNT))
                    .orElse(0)
            );

            if (completeBranchCount < passBranchCount) {
                this.executionDao.updateById(branchNodeExecution);
                return;
            }

            // 结束其他未执行完的分支
            List<FlowExecution> otherExecution = this.executionDao.selectUncompletedByParentId(branchNodeExecution.getId(), instance.getId());
            if (CollectionUtil.isNotEmpty(otherExecution)) {
                for (FlowExecution other : otherExecution) {
                    this.forceFinishExecution(other, FlowInstanceState.AUTO_COMPLETE);
                }
            }
            // 此处需指定lastExecution为当前执行记录，否则下次执行的lastExecutionId会变成branchNodeExecution的ID
            this.completeExecution(branchNodeExecution, execution);
        }
    }

    /**
     * 强制结束流程执行
     *
     * @param execution     流程执行
     * @param instanceState 实例状态
     *
     * @return 结果
     */
    private boolean forceFinishExecution(FlowExecution execution, FlowInstanceState instanceState) {
        execution.setState(FlowExecutionState.COMPLETE.getValue());
        execution.setCompletedTime(LocalDateTime.now());
        boolean forceFinishTaskResult = FlowContext.getTaskService().forceFinishTask(execution.getId(), instanceState);
        if (!forceFinishTaskResult) {
            log.error("强制结束流程执行[{}]任务失败！", execution.getId());
            return false;
        }
        boolean updateResult = this.executionDao.updateById(execution);
        if (!updateResult) {
            log.error("更新流程执行记录[{}]失败！", execution.getId());
            return false;
        }
        return true;
    }
}
