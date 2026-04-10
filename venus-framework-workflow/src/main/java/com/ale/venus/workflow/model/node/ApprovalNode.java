package com.ale.venus.workflow.model.node;

import cn.hutool.core.collection.CollectionUtil;
import com.ale.venus.common.utils.CastUtils;
import com.ale.venus.workflow.constants.FlowVariableConstants;
import com.ale.venus.workflow.entity.FlowExecution;
import com.ale.venus.workflow.entity.FlowInstance;
import com.ale.venus.workflow.entity.FlowTask;
import com.ale.venus.workflow.entity.FlowTaskActor;
import com.ale.venus.workflow.enumeration.MultiApprovalStrategy;
import com.ale.venus.workflow.enumeration.NoAssigneeStrategy;
import com.ale.venus.workflow.exception.FlowException;
import com.ale.venus.workflow.model.*;
import com.ale.venus.workflow.support.FinishTaskParam;
import com.ale.venus.workflow.support.FlowContext;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * 审批节点
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApprovalNode extends UserNode {

    /**
     * 节点类型
     */
    public static final String NODE_TYPE = "approval";

    /**
     * 多人审批方式
     */
    @JsonProperty(index = 21)
    private MultiApprovalStrategy multipleApprovalStrategy;

    /**
     * 无审批人时处理方式
     */
    @JsonProperty(index = 22)
    private NoAssigneeStrategy noAssigneeStrategy;

    /**
     * 通过权重
     */
    @JsonProperty(index = 23)
    private Integer passWeight;

    /**
     * 超时时间
     */
    @JsonProperty(index = 24)
    private LocalDateTime expireTime;

    /**
     * 提醒次数限制
     * 达到次数限制后不再提醒
     */
    @JsonProperty(index = 25)
    private Integer remindCountLimit;

    @Override
    protected Function<FlowTask, Boolean> buildTaskFunction() {
        return task -> {
            task.setExpireTime(this.getExpireTime());
            return MultiApprovalStrategy.SEQUENTIAL.match(this.multipleApprovalStrategy);
        };
    }

    @Override
    protected boolean doExecute(FlowInstance instance, FlowExecution execution, List<TaskAssignee> taskAssignees, List<FlowTask> tasks, List<FlowTaskActor> taskActors) {
        // 未找到审批人处理
        if (CollectionUtil.isEmpty(tasks)) {
            // 自动通过完成当前执行记录
            if (NoAssigneeStrategy.AUTO_COMPLETE.match(this.noAssigneeStrategy)) {
                return FlowContext.getExecutionService().completeExecution(execution);
            }
            return FlowContext.getInstanceService().reject(instance.getId(), true);
        }
        boolean addResult = FlowContext.getTaskService().addTask(tasks, taskActors, true);
        if (!addResult) {
            throw new FlowException("执行审批节点失败！无法添加审批任务！");
        }
        // 顺签设置审批人信息
        if (MultiApprovalStrategy.SEQUENTIAL.match(this.multipleApprovalStrategy)) {
            // 移除已生成任务的受理人
            taskAssignees.removeIf(
                taskAssignee -> tasks.stream().anyMatch(task -> Objects.equals(task.getAssigneeId(), taskAssignee.getId()))
            );
            execution.addVariable(FlowVariableConstants.TASK_ASSIGNEES, taskAssignees);
            FlowContext.getExecutionService().updateExecution(execution);
        }
        for (FlowTask task : tasks) {
            // 如果任务受理人为发起人则自动完成任务
            if (Objects.equals(task.getAssigneeId(), instance.getCreatedBy())) {
                boolean result = FlowContext.getTaskService().completeTask(
                    FinishTaskParam.builder()
                        .taskId(task.getId())
                        .assigneeId(task.getAssigneeId())
                        .comment("发起人自动完成任务")
                        .build()
                );
                if (!result) {
                    log.warn("任务[{}]自动完成失败！", task.getId());
                    return false;
                }
            }

        }
        return true;
    }

    /**
     * 添加审批人
     *
     * @param instance       流程实例
     * @param execution      流程执行记录
     * @param assigneeConfig 审批人配置
     * @return 结果
     */
    public boolean addAssignee(FlowInstance instance, FlowExecution execution, List<AssigneeConfig> assigneeConfig) {
        List<TaskAssignee> taskAssignees = TaskAssigneeSupport.parse(assigneeConfig);
        List<FlowTask> appendTasks = Lists.newArrayListWithCapacity(taskAssignees.size());
        List<FlowTaskActor> appendTaskActors = Lists.newArrayListWithCapacity(taskAssignees.size());

        this.createTasks(instance, execution, taskAssignees, (tasks, taskActors) -> {
            // 不为顺签才添加任务
            if (MultiApprovalStrategy.SEQUENTIAL.match(this.multipleApprovalStrategy)) {
                // 执行记录添加受理人
                List<TaskAssignee> oldTaskAssignees = CastUtils.cast(execution.getVariableByKey(FlowVariableConstants.TASK_ASSIGNEES));
                oldTaskAssignees.addAll(taskAssignees);
                execution.addVariable(FlowVariableConstants.TASK_ASSIGNEES, oldTaskAssignees);
            } else {
                appendTasks.addAll(tasks);
                appendTaskActors.addAll(taskActors);
            }
        });

        if (CollectionUtil.isNotEmpty(appendTasks) && CollectionUtil.isNotEmpty(appendTaskActors)) {
            boolean addTaskResult = FlowContext.getTaskService().addTask(appendTasks, appendTaskActors, true);
            if (!addTaskResult) {
                throw new FlowException("添加审批人失败！");
            }
        }
        return true;
    }
}
