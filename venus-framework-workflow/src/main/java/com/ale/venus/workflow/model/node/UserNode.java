package com.ale.venus.workflow.model.node;

import cn.hutool.core.util.BooleanUtil;
import com.ale.venus.workflow.entity.FlowExecution;
import com.ale.venus.workflow.entity.FlowInstance;
import com.ale.venus.workflow.entity.FlowTask;
import com.ale.venus.workflow.entity.FlowTaskActor;
import com.ale.venus.workflow.model.AssigneeConfig;
import com.ale.venus.workflow.model.TaskAssignee;
import com.ale.venus.workflow.model.TaskAssigneeSupport;
import com.ale.venus.workflow.support.FlowContext;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 用户参与节点
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class UserNode extends FlowNode {

    /**
     * 受理人配置
     */
    @JsonProperty(index = 61)
    protected List<AssigneeConfig> assigneeConfigs;

    @Override
    public boolean execute(FlowInstance flowInstance, FlowExecution lastExecution, Map<String, Object> variable) {
        FlowExecution execution = FlowContext.getExecutionService().createExecution(flowInstance, this.id, lastExecution, variable);
        List<TaskAssignee> taskAssignees = TaskAssigneeSupport.parse(this.assigneeConfigs);
        AtomicBoolean executeResult = new AtomicBoolean(true);
        this.createTasks(flowInstance, execution, taskAssignees, (tasks, taskActors) ->
            executeResult.set(this.doExecute(flowInstance, execution, taskAssignees, tasks, taskActors))
        );
        return executeResult.get();
    }

    /**
     * 创建任务
     *
     * @param instance     流程实例
     * @param execution    流程执行记录
     * @param assignees    受理人集合
     * @param taskConsumer 任务消费者
     */
    protected void createTasks(FlowInstance instance, FlowExecution execution, List<TaskAssignee> assignees, BiConsumer<List<FlowTask>, List<FlowTaskActor>> taskConsumer) {
        List<FlowTask> tasks = Lists.newArrayListWithCapacity(assignees.size());
        List<FlowTaskActor> taskActors = Lists.newArrayListWithCapacity(assignees.size());
        for (TaskAssignee taskAssignee : assignees) {
            FlowTask task = FlowTask.of(instance, this, null);
            task.setExecutionId(execution.getId());
            task.setViewed(false);
            task.setAssigneeId(taskAssignee.getId());
            Boolean termination = this.buildTaskFunction().apply(task);
            tasks.add(task);
            taskActors.add(FlowTaskActor.of(task, taskAssignee));
            if (BooleanUtil.isTrue(termination)) {
                break;
            }
        }
        taskConsumer.accept(tasks, taskActors);
    }

    /**
     * 构建任务函数
     *
     * @return 是否终止后续构建
     */
    protected abstract Function<FlowTask, Boolean> buildTaskFunction();
    
    /**
     * 执行节点
     *
     * @param instance      流程实例
     * @param execution     流程执行记录
     * @param taskAssignees 任务受理人
     * @param tasks         流程任务
     * @param taskActors    流程任务参与者
     * @return 任务执行结果
     */
    protected abstract boolean doExecute(FlowInstance instance, FlowExecution execution, List<TaskAssignee> taskAssignees, List<FlowTask> tasks, List<FlowTaskActor> taskActors);
}
