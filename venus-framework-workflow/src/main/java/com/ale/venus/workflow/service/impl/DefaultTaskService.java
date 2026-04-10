package com.ale.venus.workflow.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.transaction.TransactionSupport;
import com.ale.venus.common.utils.CastUtils;
import com.ale.venus.common.utils.JsonUtils;
import com.ale.venus.workflow.constants.FlowVariableConstants;
import com.ale.venus.workflow.dao.*;
import com.ale.venus.workflow.entity.*;
import com.ale.venus.workflow.enumeration.*;
import com.ale.venus.workflow.exception.FlowException;
import com.ale.venus.workflow.hook.TaskEventPublisher;
import com.ale.venus.workflow.hook.TaskEventType;
import com.ale.venus.workflow.model.*;
import com.ale.venus.workflow.model.node.ApprovalNode;
import com.ale.venus.workflow.model.node.FlowNode;
import com.ale.venus.workflow.service.TaskService;
import com.ale.venus.workflow.support.FinishTaskParam;
import com.ale.venus.workflow.support.FlowContext;
import com.ale.venus.workflow.support.IdGeneratorSupport;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 默认流程任务服务
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
public class DefaultTaskService implements TaskService {

    /**
     * 流程任务dao
     */
    private final FlowTaskDao taskDao;

    /**
     * 流程任务参与者dao
     */
    private final FlowTaskActorDao taskActorDao;

    /**
     * 历史流程任务dao
     */
    private final FlowHistoryTaskDao historyTaskDao;

    /**
     * 历史流程任务参与者dao
     */
    private final FlowHistoryTaskActorDao historyTaskActorDao;

    public DefaultTaskService(FlowTaskDao taskDao, FlowTaskActorDao taskActorDao, FlowHistoryTaskDao historyTaskDao, FlowHistoryTaskActorDao historyTaskActorDao) {
        this.taskDao = taskDao;
        this.taskActorDao = taskActorDao;
        this.historyTaskDao = historyTaskDao;
        this.historyTaskActorDao = historyTaskActorDao;
    }

    @Override
    public boolean addTask(List<FlowTask> flowTasks, List<FlowTaskActor> taskActors, boolean isActive) {
        if (isActive) {
            Map<String, List<FlowTaskActor>> taskActorMapping = taskActors.stream()
                .collect(Collectors.groupingBy(FlowTaskActor::getTaskId));
            boolean result = true;
            for (FlowTask flowTask : flowTasks) {
                List<FlowTaskActor> flowTaskActors = taskActorMapping.get(flowTask.getId());
                TaskEventPublisher.publishTaskEvent(TaskEventType.BEFORE_CREATE, flowTask, flowTaskActors);
                boolean executeResult = this.taskDao.insert(flowTask) && this.taskActorDao.batchInsert(flowTaskActors);
                if (!executeResult) {
                    result = false;
                    break;
                }
                TaskEventPublisher.publishTaskEvent(TaskEventType.AFTER_CREATE, flowTask, flowTaskActors);
            }
            return result;
        }
        return TransactionSupport.execute(() -> {
            boolean insertTaskResult = this.historyTaskDao.batchInsert(flowTasks.stream()
                .map(FlowHistoryTask::of)
                .collect(Collectors.toList())
            );
            if (CollectionUtil.isNotEmpty(taskActors)) {
                boolean insertTaskActorResult = this.historyTaskActorDao.batchInsert(taskActors.stream()
                    .map(FlowHistoryTaskActor::of)
                    .collect(Collectors.toList())
                );
                return insertTaskResult && insertTaskActorResult;
            }
            return insertTaskResult;
        });
    }


    @Override
    public boolean updateTask(FlowTask flowTask) {
        List<FlowTaskActor> taskActors = this.taskActorDao.selectByTaskId(flowTask.getId());
        TaskEventPublisher.publishTaskEvent(TaskEventType.BEFORE_UPDATE, flowTask, taskActors);
        boolean updateResult = this.taskDao.updateById(flowTask);
        if (!updateResult) {
            throw new FlowException("更新流程任务[{}]失败！", flowTask.getId());
        }
        TaskEventPublisher.publishTaskEvent(TaskEventType.AFTER_UPDATE, flowTask, taskActors);
        return true;
    }

    @Override
    public boolean addVariable(String taskId, Map<String, Object> variable) {
        FlowTask flowTask = this.fetchTaskAndCheckExists(taskId);
        flowTask.addVariable(variable);
        return this.updateTask(flowTask);
    }

    @Override
    public boolean completeTask(FinishTaskParam completeTaskParam) {
        return this.finishTask(
            completeTaskParam,
            FlowTaskState.COMPLETE,
            TaskEventType.BEFORE_COMPLETE,
            TaskEventType.AFTER_COMPLETE,
            (historyTask, historyTaskActor) -> {
                if (log.isDebugEnabled()) {
                    log.debug("任务[{}]完成！", historyTask.getId());
                }
                // 任务完成后置处理
                this.afterTaskComplete(
                    historyTask,
                    historyTaskActor,
                    completeTaskParam.getVariable(),
                    completeTaskParam.getVariableLevel()
                );
            }
        );
    }

    @Override
    public boolean forceFinishTask(String executionId, FlowInstanceState instanceState) {
        List<FlowTask> activeTasks = this.taskDao.selectByExecutionId(executionId);
        if (activeTasks.isEmpty()) {
            return true;
        }
        String comment = (instanceState == null) ? null : switch (instanceState) {
            case COMPLETE -> "其他人完成任务，自动结束";
            case REJECT -> "其他人驳回审批，自动结束";
            case REVOKE -> "发起人撤回审批，自动结束";
            case TIMEOUT -> "任务超时，自动结束";
            case ROLLBACK -> "流程回退，自动结束";
            case AUTO_REJECT -> "流程自动驳回";
            case AUTO_COMPLETE -> "流程自动完成";
            default -> null;
        };
        List<FlowHistoryTask> historyTasks = activeTasks.stream()
            .map(task -> FlowHistoryTask.of(task, FlowTaskState.AUTO_FINISH))
            .collect(Collectors.toList());
        List<FlowTaskActor> taskActors = this.taskActorDao.selectByExecutionId(executionId);
        List<FlowHistoryTaskActor> historyTaskActors = taskActors.stream()
            .map(taskActor -> {
                FlowHistoryTaskActor historyTaskActor = FlowHistoryTaskActor.of(taskActor);
                historyTaskActor.setComment(comment);
                return historyTaskActor;
            })
            .collect(Collectors.toList());
        return TransactionSupport.execute(() -> BooleanUtil.and(
            this.historyTaskDao.batchInsert(historyTasks),
            this.historyTaskActorDao.batchInsert(historyTaskActors),
            this.taskDao.deleteByExecutionId(executionId),
            this.taskActorDao.deleteByExecutionId(executionId)
        ));
    }

    @Override
    public boolean suspendTask(String executionId) {
        List<FlowTask> tasks = this.taskDao.selectByExecutionIdAndState(executionId, FlowTaskState.ACTIVE);
        tasks.forEach(task -> task.setState(FlowTaskState.SUSPEND.getValue()));
        return this.taskDao.batchUpdate(tasks);
    }

    @Override
    public boolean activeTask(String executionId) {
        List<FlowTask> tasks = this.taskDao.selectByExecutionIdAndState(executionId, FlowTaskState.SUSPEND);
        tasks.forEach(task -> task.setState(FlowTaskState.ACTIVE.getValue()));
        return this.taskDao.batchUpdate(tasks);
    }

    @Override
    public boolean rollbackTask(String rollbackNodeId, Boolean jumpCurrentNode, FinishTaskParam rollbackTaskParam) {
        return this.finishTask(
            rollbackTaskParam,
            FlowTaskState.ROLLBACK,
            TaskEventType.BEFORE_ROLLBACK,
            TaskEventType.AFTER_ROLLBACK,
            (historyTask, historyTaskActor) -> {

                InstanceModel instanceModel = InstanceModelSupport.parseInstanceModel(historyTask.getInstanceId());
                FlowExecution execution = FlowContext.getExecutionService().fetchExecution(historyTask.getExecutionId());

                FlowNode rollbackNode = instanceModel.findNode(rollbackNodeId);
                if (rollbackNode == null) {
                    throw new FlowException("回退失败！回退[{}]节点不存在！", rollbackNodeId);
                }

                Map<String, Object> variable = rollbackTaskParam.getVariable();
                if (CollectionUtil.isEmpty(variable)) {
                    variable = new HashMap<>();
                }
                FlowNode currentNode = instanceModel.findNode(historyTask.getNodeId());
                // 允许返回同级节点
                boolean enableRollback = StrUtil.isBlank(rollbackNode.getBranchId()) && StrUtil.isBlank(currentNode.getBranchId())
                    || Objects.equals(currentNode.getBranchId(), rollbackNode.getBranchId());
                if (!enableRollback) {
                    throw new FlowException("回退失败！回退节点只能为同级节点！");
                }

                if (BooleanUtil.isTrue(jumpCurrentNode)) {
                    variable.put(FlowVariableConstants.JUMP_EXECUTION_ID, execution.getId());
                    String jumpExecutionId = CastUtils.cast(execution.getVariableByKey(FlowVariableConstants.JUMP_EXECUTION_ID));
                    // 结束当前执行
                    this.forceFinishTask(execution.getId(), FlowInstanceState.ROLLBACK);
                    execution.setState(FlowExecutionState.COMPLETE.getValue());
                    execution.setCompletedTime(LocalDateTime.now());
                    FlowContext.getExecutionService().updateExecution(execution);
                    // 不存在跳转执行ID即为第一次回退，需要挂起激活的执行记录
                    if (StrUtil.isBlank(jumpExecutionId)) {
                        FlowContext.getExecutionService().suspendExecution(execution.getInstanceId());
                    }
                } else {
                    // 回退节点为顶级节点则结束所有执行
                    if (StrUtil.isBlank(rollbackNode.getBranchId())) {
                        FlowContext.getExecutionService().forceFinishExecution(historyTask.getInstanceId(), FlowInstanceState.ROLLBACK);
                    } else {
                        this.forceFinishTask(execution.getId(), FlowInstanceState.ROLLBACK);
                        execution.setState(FlowExecutionState.COMPLETE.getValue());
                        execution.setCompletedTime(LocalDateTime.now());
                        FlowContext.getExecutionService().updateExecution(execution);
                    }
                }

                // 更新流程实例执行节点信息
                FlowInstance instance = instanceModel.getFlowInstance();
                instance.setCurrentNodeId(rollbackNodeId);
                FlowContext.getInstanceService().updateInstance(instance);

                // 执行回退节点
                rollbackNode.execute(instance, execution, variable);
            }
        );
    }

    @Override
    public boolean rejectTask(FinishTaskParam rejectTaskParam) {
        return this.finishTask(
            rejectTaskParam,
            FlowTaskState.REJECT,
            TaskEventType.BEFORE_REJECT,
            TaskEventType.AFTER_REJECT,
            (historyTask, historyTaskActor) -> {
                // 驳回流程实例
                boolean rejectResult = FlowContext.getInstanceService().reject(historyTask.getInstanceId());

                if (!rejectResult) {
                    throw new FlowException("流程实例[{}]驳回失败！", historyTask.getInstanceId());
                }

                if (log.isDebugEnabled()) {
                    log.debug("任务[{}]被驳回！", historyTask.getId());
                }
            }
        );
    }

    @Override
    public boolean transferTask(String assigneeId, FinishTaskParam transferTaskParam) {
        return this.agentTask(
            assigneeId,
            transferTaskParam,
            AssigneeType.TRANSFER
        );
    }

    @Override
    public boolean delegateTask(String assigneeId, FinishTaskParam delegateTaskParam) {
        return this.agentTask(
            assigneeId,
            delegateTaskParam,
            AssigneeType.DELEGATE
        );
    }

    @Override
    public boolean reclaimTask(String taskId, String assigneeId) {
        // 查询任务是否已办理
        FlowTask activeTask = this.taskDao.selectById(taskId);
        if (activeTask == null) {
            FlowHistoryTask historyTask = this.historyTaskDao.selectById(taskId);
            if (historyTask != null) {
                throw new FlowException("任务[{}]已办理！无法拿回！", taskId);
            }
            throw new FlowException("任务[{}]不存在！", taskId);
        }
        FlowHistoryTaskActor historyTaskActor = this.historyTaskActorDao.selectByTaskIdAndActorId(taskId, assigneeId);
        if (historyTaskActor == null) {
            throw new FlowException("用户不是任务[{}]的原受理人，无法拿回任务！", taskId);
        }
        FlowTaskActor taskActor = this.taskActorDao.selectByTaskIdAndActorId(taskId, activeTask.getAssigneeId());
        if (taskActor == null) {
            throw new FlowException("拿回任务[{}]失败，未找到代理人！", taskId);
        }
        FlowTaskActor originalActor = FlowTaskActor.of(
            activeTask,
            TaskAssignee.builder()
                .id(assigneeId)
                .weight(taskActor.getWeight())
                .build()
        );

        // 修改受理人
        activeTask.setAssigneeId(assigneeId);
        activeTask.setOwnerId(null);

        return TransactionSupport.execute(() -> BooleanUtil.and(
            this.historyTaskActorDao.deleteById(historyTaskActor.getId()),
            this.taskActorDao.deleteById(taskActor.getId()),
            this.taskActorDao.insert(originalActor),
            this.taskDao.updateById(activeTask)
        ));
    }

    @Override
    public boolean revokeTask(String taskId) {
        FlowHistoryTask historyTask = this.historyTaskDao.selectById(taskId);
        if (historyTask == null) {
            throw new FlowException("撤回[{}]任务失败！任务不存在！", taskId);
        }
        FlowHistoryTaskActor historyTaskActor = this.historyTaskActorDao.selectByTaskIdAndActorId(taskId, historyTask.getAssigneeId());
        InstanceModel instanceModel = InstanceModelSupport.parseInstanceModel(historyTask.getInstanceId());
        FlowInstance instance = instanceModel.getFlowInstance();
        ApprovalNode revokeNode = CastUtils.cast(instanceModel.findNode(historyTask.getNodeId()));
        if (revokeNode == null) {
            throw new FlowException("撤回[{}]任务失败！任务节点不存在！", taskId);
        }
        // 当前执行已结束
        if (!Objects.equals(historyTask.getNodeId(), instance.getCurrentNodeId())) {
            throw new FlowException("撤回[{}]任务失败！当前流程已进入后续节点！", taskId);
        }
        // 会签则直接恢复任务
        if (MultiApprovalStrategy.JOINT.match(revokeNode.getMultipleApprovalStrategy())) {
            return this.restoreTask(historyTask, historyTaskActor);
        } else if (MultiApprovalStrategy.SEQUENTIAL.match(revokeNode.getMultipleApprovalStrategy())) {
            // 顺签需要删除现有的任务再恢复任务
            FlowExecution execution = FlowContext.getExecutionService().fetchExecution(historyTask.getExecutionId());
            List<FlowTaskActor> activeTaskActors = this.taskActorDao.selectByExecutionId(historyTask.getExecutionId());
            List<TaskAssignee> taskAssignees = CastUtils.cast(execution.getVariableByKey(FlowVariableConstants.TASK_ASSIGNEES));
            taskAssignees.addAll(activeTaskActors.stream()
                .map(taskActor -> TaskAssignee.builder()
                    .id(taskActor.getActorId())
                    .weight(taskActor.getWeight())
                    .build()
                ).toList()
            );
            execution.addVariable(FlowVariableConstants.TASK_ASSIGNEES, taskAssignees);
            return TransactionSupport.execute(() -> {
                boolean executeResult = BooleanUtil.and(
                    FlowContext.getExecutionService().updateExecution(execution),
                    this.taskDao.deleteByExecutionId(historyTask.getExecutionId()),
                    this.taskActorDao.deleteByExecutionId(historyTask.getExecutionId()),
                    this.restoreTask(historyTask, historyTaskActor)
                );
                if (!executeResult) {
                    throw new FlowException("撤回[{}]任务失败！", taskId);
                }
                return true;
            });
        } else if (MultiApprovalStrategy.VOTE.match(revokeNode.getMultipleApprovalStrategy())) {
            // 票签需要减去权重再恢复任务
            FlowExecution execution = FlowContext.getExecutionService().fetchExecution(historyTask.getExecutionId());
            Integer accumulativeWeight = CastUtils.cast(execution.getVariableByKey(FlowVariableConstants.ACCUMULATIVE_WEIGHT));
            accumulativeWeight -= historyTaskActor.getWeight();
            execution.addVariable(FlowVariableConstants.ACCUMULATIVE_WEIGHT, accumulativeWeight);
            FlowContext.getExecutionService().updateExecution(execution);
            return this.restoreTask(historyTask, historyTaskActor);
        }
        return true;
    }

    @Override
    public boolean addAssignee(String taskId, List<AssigneeConfig> assigneeConfig) {
        FlowTask task = this.fetchTaskAndCheckExists(taskId);
        InstanceModel instanceModel = InstanceModelSupport.parseInstanceModel(task.getInstanceId());
        FlowInstance instance = instanceModel.getFlowInstance();
        FlowNode flowNode = instanceModel.findNode(task.getNodeId());
        if (flowNode instanceof ApprovalNode approvalNode) {
            boolean addAssigneeResult = approvalNode.addAssignee(
                instance,
                FlowContext.getExecutionService().fetchExecution(task.getExecutionId()),
                assigneeConfig
            );
            if (!addAssigneeResult) {
                throw new FlowException("添加审批人失败！");
            }
        } else {
            throw new FlowException("节点[{}]不是审批节点，无法添加审批人！", flowNode.getName());
        }
        return true;
    }

    @Override
    public boolean removeAssignee(String taskId, List<String> assigneeIds) {
        FlowTask task = this.fetchTaskAndCheckExists(taskId);
        InstanceModel instanceModel = InstanceModelSupport.parseInstanceModel(task.getInstanceId());
        ApprovalNode currentNode = CastUtils.cast(instanceModel.findNode(task.getNodeId()));
        // 顺签需要从执行记录中获取剩余受理人
        if (MultiApprovalStrategy.SEQUENTIAL.match(currentNode.getMultipleApprovalStrategy())) {
            FlowExecution execution = FlowContext.getExecutionService().fetchExecution(task.getExecutionId());
            List<TaskAssignee> taskAssignees = JsonUtils.fromJsonList(
                JsonUtils.toJson(execution.getVariableByKey(FlowVariableConstants.TASK_ASSIGNEES)),
                TaskAssignee.class
            );
            Map<String, TaskAssignee> restAssigneeMapping = taskAssignees.stream()
                .collect(Collectors.toMap(TaskAssignee::getId, Function.identity()));
            for (String assigneeId : assigneeIds) {
                TaskAssignee removeAssignee = restAssigneeMapping.get(assigneeId);
                if (removeAssignee != null) {
                    taskAssignees.remove(removeAssignee);
                }
            }
            execution.addVariable(FlowVariableConstants.TASK_ASSIGNEES, taskAssignees);
            return FlowContext.getExecutionService().updateExecution(execution);
        }
        List<FlowTask> activeTasks = this.taskDao.selectByExecutionId(task.getExecutionId());
        if (CollectionUtil.isEmpty(activeTasks)) {
            throw new FlowException("移除审批人失败！当前执行无进行中的审批任务！");
        }
        Map<String, String> assigneeMapping = activeTasks.stream()
            .collect(Collectors.toMap(FlowTask::getAssigneeId, FlowTask::getId));
        List<String> removeTaskIds = Lists.newArrayList();
        for (String assigneeId : assigneeIds) {
            if (Objects.equals(assigneeId, task.getAssigneeId())) {
                throw new FlowException("移除受理人失败！不能移除自己！", assigneeId);
            }
            String removeTaskId = assigneeMapping.get(assigneeId);
            if (StrUtil.isBlank(removeTaskId)) {
                continue;
            }
            removeTaskIds.add(removeTaskId);
        }
        if (removeTaskIds.isEmpty()) {
            return true;
        }
        return TransactionSupport.execute(() -> {
            boolean executeResult = BooleanUtil.and(
                this.taskDao.deleteByIds(removeTaskIds),
                this.taskActorDao.deleteByTaskIds(removeTaskIds)
            );
            if (!executeResult) {
                throw new FlowException("移除审批人失败！");
            }
            return true;
        });
    }

    @Override
    public boolean handoverAllTask(String originAssigneeId, String targetAssigneeId) {
        List<FlowTask> tasks = this.taskDao.selectByAssigneeId(originAssigneeId, true);
        tasks.forEach(task -> {
            if (Objects.equals(task.getOwnerId(), originAssigneeId)) {
                task.setOwnerId(originAssigneeId);
                return;
            }
            if (Objects.equals(task.getAssigneeId(), originAssigneeId)) {
                task.setAssigneeId(targetAssigneeId);
            }
        });
        List<FlowTaskActor> taskActors = this.taskActorDao.selectByActorId(originAssigneeId, true);
        taskActors.forEach(taskActor -> {
            if (Objects.equals(taskActor.getAssigneeId(), originAssigneeId)) {
                taskActor.setAssigneeId(originAssigneeId);
                return;
            }
            if (Objects.equals(taskActor.getActorId(), originAssigneeId)) {
                taskActor.setActorId(targetAssigneeId);
            }
        });
        return TransactionSupport.execute(() -> {
            if (CollectionUtil.isNotEmpty(tasks)) {
                if (!this.taskDao.batchUpdate(tasks)) {
                    throw new FlowException("移交任务失败！");
                }
            }
            if (CollectionUtil.isNotEmpty(taskActors)) {
                if (!this.taskActorDao.batchUpdate(taskActors)) {
                    throw new FlowException("移交任务失败！");
                }
            }
            return true;
        });
    }

    @Override
    public boolean isHistoryAssignee(String instanceId, String assigneeId) {
        return this.historyTaskDao.exists(instanceId, assigneeId);
    }

    /**
     * 获取流程任务并检查流程任务是否存在
     *
     * @param taskId 流程任务ID
     * @return 流程任务
     */
    private FlowTask fetchTaskAndCheckExists(String taskId) {
        FlowTask flowTask = this.taskDao.selectById(taskId);
        if (flowTask == null) {
            throw new FlowException("流程任务[{}]不存在！", taskId);
        }
        return flowTask;
    }

    /**
     * 检查流程任务是否允许执行
     *
     * @param flowTask   流程任务
     * @param assigneeId 流程任务处理人ID
     */
    private void checkTaskAllowExecution(FlowTask flowTask, String assigneeId) {
        FlowContext.getInstanceService().checkInstanceAllowExecution(flowTask.getInstanceId());
        if (!Objects.equals(flowTask.getAssigneeId(), assigneeId)) {
            log.error("处理任务[{}]失败！处理人：{}", flowTask.getId(), assigneeId);
            throw new FlowException("用户不是该任务受理人！无法处理任务！");
        }
    }

    /**
     * 代理任务
     *
     * @param assigneeId      受理人ID
     * @param finishTaskParam 任务参数
     * @param assigneeType    受理类型
     * @return 结果
     */
    private boolean agentTask(String assigneeId, FinishTaskParam finishTaskParam, AssigneeType assigneeType) {
        FlowTask flowTask = this.fetchTaskAndCheckExists(finishTaskParam.getTaskId());
        this.checkTaskAllowExecution(flowTask, finishTaskParam.getAssigneeId());
        // 获取原始任务受理人
        FlowTaskActor originalActor = this.taskActorDao.selectByTaskIdAndActorId(flowTask.getId(), finishTaskParam.getAssigneeId());
        if (originalActor == null) {
            if (assigneeType == AssigneeType.DELEGATE) {
                throw new FlowException("委派任务失败！未找到原任务受理人！");
            }
            throw new FlowException("转办任务失败！未找到原任务受理人！");
        }

        TaskEventType beforeTaskEvent;
        TaskEventType afterTaskEvent;
        if (assigneeType == AssigneeType.TRANSFER) {
            beforeTaskEvent = TaskEventType.BEFORE_TRANSFER;
            afterTaskEvent = TaskEventType.AFTER_TRANSFER;
        } else {
            afterTaskEvent = TaskEventType.AFTER_DELEGATE;
            beforeTaskEvent = TaskEventType.BEFORE_DELEGATE;
        }

        return TransactionSupport.execute(() -> {
            TaskEventPublisher.publishTaskEvent(beforeTaskEvent, flowTask, List.of(originalActor));

            if (assigneeType == AssigneeType.DELEGATE && StrUtil.isBlank(flowTask.getOwnerId())) {
                flowTask.setOwnerId(flowTask.getAssigneeId());
            }
            flowTask.setAssigneeId(assigneeId);

            originalActor.setAssigneeId(assigneeId);
            originalActor.setAssigneeType(assigneeType.getValue());

            FlowHistoryTaskActor historyTaskActor = FlowHistoryTaskActor.of(originalActor);
            FlowTaskActor assigneeActor = FlowTaskActor.of(
                flowTask,
                TaskAssignee.builder()
                    .id(assigneeId)
                    .weight(originalActor.getWeight())
                    .build()
            );
            historyTaskActor.setComment(finishTaskParam.getComment());

            boolean executeResult = BooleanUtil.and(
                this.taskActorDao.deleteById(originalActor.getId()),
                this.historyTaskActorDao.insert(historyTaskActor),
                this.taskActorDao.insert(assigneeActor),
                this.taskDao.updateById(flowTask)
            );
            if (!executeResult) {
                throw new FlowException("任务[{}]委派失败！", flowTask.getId());
            }
            TaskEventPublisher.publishTaskEvent(afterTaskEvent, flowTask, List.of(assigneeActor));
            return true;
        });
    }

    /**
     * 结束任务
     *
     * @param finishTaskParam 结束任务参数
     * @param state           任务状态
     * @param beforeEvent     任务结束前置事件
     * @param afterEvent      任务结束后置事件
     * @param afterTaskFinish 任务结束后置处理
     * @return 结果
     */
    private boolean finishTask(FinishTaskParam finishTaskParam, FlowTaskState state, TaskEventType beforeEvent, TaskEventType afterEvent, BiConsumer<FlowHistoryTask, FlowHistoryTaskActor> afterTaskFinish) {
        FlowTask task = this.fetchTaskAndCheckExists(finishTaskParam.getTaskId());
        this.checkTaskAllowExecution(task, finishTaskParam.getAssigneeId());
        List<FlowTaskActor> taskActors = this.taskActorDao.selectByTaskId(task.getId());

        if (VariableLevel.TASK.match(finishTaskParam.getVariableLevel())) {
            task.addVariable(finishTaskParam.getVariable());
        }

        // 找到任务实际受理人
        FlowHistoryTaskActor historyTaskActor = taskActors.stream()
            .filter(taskActor -> Objects.equals(taskActor.getActorId(), finishTaskParam.getAssigneeId()))
            .findFirst()
            .map(taskActor -> {
                FlowHistoryTaskActor flowHistoryTaskActor = FlowHistoryTaskActor.of(taskActor);
                flowHistoryTaskActor.setComment(finishTaskParam.getComment());
                if (CollectionUtil.isNotEmpty(finishTaskParam.getAttachments())) {
                    flowHistoryTaskActor.setAttachments(JSON.toJSONString(finishTaskParam.getAttachments()));
                }
                return flowHistoryTaskActor;
            })
            .orElseThrow(() -> new FlowException("受理人不在任务参与人中！"));

        return TransactionSupport.execute(() -> {
            TaskEventPublisher.publishTaskEvent(beforeEvent, task, taskActors);

            // 如果任务为委派出去的任务，则将任务归还给原受理人
            if (StrUtil.isNotBlank(task.getOwnerId())) {
                task.setAssigneeId(task.getOwnerId());
                task.setOwnerId(null);
            }

            FlowHistoryTask historyTask = FlowHistoryTask.of(task, state);
            if (VariableLevel.TASK.match(finishTaskParam.getVariableLevel())) {
                historyTask.addVariable(finishTaskParam.getVariable());
            }

            boolean executeResult = BooleanUtil.and(
                this.historyTaskDao.insert(historyTask),
                this.taskDao.deleteById(task.getId()),
                this.historyTaskActorDao.insert(historyTaskActor),
                this.taskActorDao.deleteByTaskId(task.getId())
            );

            if (executeResult) {
                if (afterTaskFinish != null) {
                    afterTaskFinish.accept(historyTask, historyTaskActor);
                }
                TaskEventPublisher.publishTaskEvent(afterEvent, historyTask, taskActors);
            }
            return executeResult;
        });
    }

    /**
     * 任务完成后置处理
     *
     * @param historyTask      历史任务
     * @param historyTaskActor 历史任务处理人
     * @param variable         变量
     * @param variableLevel    变量级别
     */
    private void afterTaskComplete(FlowHistoryTask historyTask, FlowHistoryTaskActor historyTaskActor, Map<String, Object> variable, VariableLevel variableLevel) {
        InstanceModel instanceModel = InstanceModelSupport.parseInstanceModel(historyTask.getInstanceId());
        ApprovalNode approvalNode = CastUtils.cast(instanceModel.findNode(historyTask.getNodeId()));

        FlowInstance instance = instanceModel.getFlowInstance();
        // 因为存在分支执行，因此每次任务完成都需要流程当前执行节点信息设置为最新
        instance.setCurrentNodeId(approvalNode.getId());

        FlowExecution currentExecution = FlowContext.getExecutionService().fetchExecution(historyTask.getExecutionId());
        if (CollectionUtil.isNotEmpty(variable)) {
            if (VariableLevel.EXECUTION.match(variableLevel)) {
                currentExecution.addVariable(variable);
                FlowContext.getExecutionService().updateExecution(currentExecution);
            } else if (VariableLevel.INSTANCE.match(variableLevel)) {
                instance.addVariable(variable);
            }
        }
        FlowContext.getInstanceService().updateInstance(instance);

        // 顺签，存在下一个审批人则生成任务，否则完成执行
        if (MultiApprovalStrategy.SEQUENTIAL.match(approvalNode.getMultipleApprovalStrategy())) {
            TaskAssignee nextTaskAssignee = FlowContext.getExecutionService().getNextTaskAssignee(currentExecution.getId());
            if (nextTaskAssignee == null) {
                FlowContext.getExecutionService().completeExecution(currentExecution);
            } else {
                FlowTask nextTask = FlowTask.of(instanceModel.getFlowInstance(), approvalNode, null);
                nextTask.setExecutionId(currentExecution.getId());
                nextTask.setViewed(false);
                nextTask.setAssigneeId(nextTaskAssignee.getId());
                nextTask.setExpireTime(approvalNode.getExpireTime());
                this.addTask(CollectionUtil.newArrayList(nextTask), CollectionUtil.newArrayList(FlowTaskActor.of(nextTask, nextTaskAssignee)), true);
            }
            return;
        }
        if (MultiApprovalStrategy.JOINT.match(approvalNode.getMultipleApprovalStrategy())) {
            int activeTaskCount = this.taskDao.countByExecutionId(currentExecution.getId());
            // 所有任务已完成
            if (activeTaskCount == 0) {
                FlowContext.getExecutionService().completeExecution(currentExecution);
            }
            return;
        }
        // 或签有人同意则直接完成本次执行
        if (MultiApprovalStrategy.ALTERNATIVE.match(approvalNode.getMultipleApprovalStrategy())) {
            // 自动通过其他人的任务
            this.forceFinishTask(historyTask.getExecutionId(), FlowInstanceState.COMPLETE);
            FlowContext.getExecutionService().completeExecution(currentExecution);
            return;
        }
        // 票签判断累计权重是否达到通过权重
        if (MultiApprovalStrategy.VOTE.match(approvalNode.getMultipleApprovalStrategy())) {
            Integer accumulativeWeight = CastUtils.cast(
                Optional.ofNullable(currentExecution.getVariableByKey(FlowVariableConstants.ACCUMULATIVE_WEIGHT))
                    .orElse(0)
            );
            accumulativeWeight += historyTaskActor.getWeight();
            if (accumulativeWeight >= approvalNode.getPassWeight()) {
                // 自动通过其他人的任务
                this.forceFinishTask(historyTask.getExecutionId(), FlowInstanceState.COMPLETE);
                FlowContext.getExecutionService().completeExecution(currentExecution);
            } else {
                currentExecution.addVariable(FlowVariableConstants.ACCUMULATIVE_WEIGHT, accumulativeWeight);
            }
        }
    }

    /**
     * 恢复任务
     *
     * @param historyTask       历史任务
     * @param historyTaskActor  历史任务参与者
     * @return 结果
     */
    private boolean restoreTask(FlowHistoryTask historyTask, FlowHistoryTaskActor historyTaskActor) {
        FlowTask task = BeanUtil.copyProperties(historyTask, FlowTask.class, "id");
        task.setId(IdGeneratorSupport.generateId());
        FlowTaskActor taskActor = BeanUtil.copyProperties(historyTaskActor, FlowTaskActor.class, "id");
        taskActor.setId(IdGeneratorSupport.generateId());
        taskActor.setTaskId(task.getId());
        return TransactionSupport.execute(() -> {
            historyTask.setState(FlowTaskState.REVOKE.getValue());
            boolean executeResult = BooleanUtil.and(
                this.taskDao.insert(task),
                this.taskActorDao.insert(taskActor),
                this.historyTaskDao.updateById(historyTask)
            );
            if (!executeResult) {
                throw new FlowException("恢复任务失败！");
            }
            return true;
        });
    }
}
